package org.cybercaelum.household_management.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.domain.DisposalResult;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundQueryResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.core.RocketMQClientTemplate;
import org.cybercaelum.household_management.constant.*;
import org.cybercaelum.household_management.context.BaseContext;
import org.cybercaelum.household_management.exception.*;
import org.cybercaelum.household_management.mapper.*;
import org.cybercaelum.household_management.pojo.dto.*;
import org.cybercaelum.household_management.pojo.entity.*;
import org.cybercaelum.household_management.pojo.vo.*;
import org.cybercaelum.household_management.service.OrderService;
import org.cybercaelum.household_management.service.RecruitmentService;
import org.cybercaelum.household_management.utils.WechatPayUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 订单服务类
 * @date 2026/3/9 上午10:56
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final RecruitmentMapper recruitmentMapper;
    private final OrderMapper orderMapper;
    private final DailyConfirmationMapper dailyConfirmationMapper;
    private final CancelApplicationMapper cancelApplicationMapper;
    private final SettlementMapper settlementMapper;
    private final RecruitmentService recruitmentService;
    private final WechatPayUtil wechatPayUtil;
    private final RocketMQClientTemplate rocketMQClientTemplate;
    private final DisputeResolutionMapper disputeResolutionMapper;

    /**
     * @description 结算计算结果内部类
     * 统一封装订单结算的各项金额计算结果
     **/
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.Builder
    public static class SettlementCalculationResult {
        private Integer workDays;                    // 实际工作天数
        private Integer totalDays;                   // 订单总天数
        private Integer unworkedDays;                // 未工作天数
        private BigDecimal dailyRate;                // 日薪
        private BigDecimal baseEarnings;             // 基础工作所得 = 工作天数 × 日薪
        private BigDecimal penalty;                  // 违约金（正数表示雇主付给雇员，负数表示雇员被扣除）
        private BigDecimal employeeFinal;            // 雇员最终所得
        private BigDecimal commission;               // 平台佣金
        private BigDecimal employerPayable;          // 雇主应支付金额
        private BigDecimal refundAmount;             // 退款金额（订单总额 - 雇主应支付）
        private Integer defaultingParty;             // 违约方
        private String calculationType;              // 计算类型描述
    }

    /**
     * @description 统一结算计算方法
     * 根据订单和结算场景计算各项金额
     * @author CyberCaelum
     * @date 2026/3/21
     * @param order 订单信息
     * @param settlementType 结算类型：1-正常完成，2-协商取消，3-全额退款，4-部分结算
     * @param defaultingParty 违约方：1-雇主，2-雇员，null-无违约方
     * @return SettlementCalculationResult 结算计算结果
     **/
    private SettlementCalculationResult calculateSettlement(Order order, int settlementType, Integer defaultingParty) {
        // 统计工作天数
        Integer workDays = dailyConfirmationMapper.countConfirmedDays(order.getId());
        Integer totalDays = order.getDays();
        Integer unworkedDays = totalDays - workDays;
        BigDecimal dailyRate = order.getPrice();
        
        // 基础工作所得
        BigDecimal baseEarnings = dailyRate.multiply(new BigDecimal(workDays));
        BigDecimal penalty = BigDecimal.ZERO;
        BigDecimal employeeFinal = BigDecimal.ZERO;
        BigDecimal commission = BigDecimal.ZERO;
        BigDecimal employerPayable = BigDecimal.ZERO;
        BigDecimal refundAmount = BigDecimal.ZERO;
        String calculationType;
        
        switch (settlementType) {
            case 1: // 正常完成结算
                employeeFinal = baseEarnings;
                commission = employeeFinal.multiply(OrderRateConstant.PLATFORM_COMMISSION_RATE);
                employerPayable = employeeFinal.add(commission);
                calculationType = "正常完成结算";
                break;
                
            case 2: // 协商一致取消 - 按实际工作天数结算
                employeeFinal = baseEarnings;
                commission = employeeFinal.multiply(OrderRateConstant.PLATFORM_COMMISSION_RATE);
                employerPayable = employeeFinal.add(commission);
                refundAmount = order.getTotal().subtract(employerPayable);
                if (refundAmount.compareTo(BigDecimal.ZERO) < 0) {
                    refundAmount = BigDecimal.ZERO;
                }
                calculationType = "协商一致取消结算";
                break;
                
            case 3: // 全额退款（平台裁决同意取消或拒单）
                employeeFinal = BigDecimal.ZERO;
                commission = BigDecimal.ZERO;
                employerPayable = BigDecimal.ZERO;
                refundAmount = order.getTotal();
                calculationType = "全额退款结算";
                break;
                
            case 4: // 部分结算（平台裁决）
                if (defaultingParty == null) {
                    // 无违约方：正常部分结算
                    employeeFinal = baseEarnings;
                    commission = employeeFinal.multiply(OrderRateConstant.PLATFORM_COMMISSION_RATE);
                    employerPayable = employeeFinal.add(commission);
                    calculationType = "部分结算（无违约方）";
                } else if (DisputeResolutionConstant.EMPLOYER_DEFAULTING.equals(defaultingParty)) {
                    // 雇主违约：雇员获得违约金补偿
                    penalty = dailyRate.multiply(new BigDecimal(unworkedDays))
                            .multiply(OrderRateConstant.PENALTY_RATE);
                    employeeFinal = baseEarnings.add(penalty);
                    commission = employeeFinal.multiply(OrderRateConstant.PLATFORM_COMMISSION_RATE);
                    employerPayable = employeeFinal.add(commission);
                    calculationType = "部分结算（雇主违约）";
                } else if (DisputeResolutionConstant.EMPLOYEE_DEFAULTING.equals(defaultingParty)) {
                    // 雇员违约：扣除违约金
                    penalty = dailyRate.multiply(new BigDecimal(unworkedDays))
                            .multiply(OrderRateConstant.PENALTY_RATE);
                    employeeFinal = baseEarnings.subtract(penalty);
                    if (employeeFinal.compareTo(BigDecimal.ZERO) < 0) {
                        employeeFinal = BigDecimal.ZERO;
                    }
                    commission = employeeFinal.multiply(OrderRateConstant.PLATFORM_COMMISSION_RATE);
                    employerPayable = employeeFinal.add(commission);
                    calculationType = "部分结算（雇员违约）";
                } else {
                    // 未知违约方，按无违约方处理
                    employeeFinal = baseEarnings;
                    commission = employeeFinal.multiply(OrderRateConstant.PLATFORM_COMMISSION_RATE);
                    employerPayable = employeeFinal.add(commission);
                    calculationType = "部分结算（未知违约方，按无违约处理）";
                }
                
                refundAmount = order.getTotal().subtract(employerPayable);
                if (refundAmount.compareTo(BigDecimal.ZERO) < 0) {
                    refundAmount = BigDecimal.ZERO;
                }
                break;
                
            default:
                throw new OrderStatusErrorException("未知的结算类型: " + settlementType);
        }
        
        log.info("结算计算完成：类型=[{}]，订单ID=[{}]，工作天数=[{}]，雇员所得=[{}]，雇主支付=[{}]，退款金额=[{}]",
                calculationType, order.getId(), workDays, employeeFinal, employerPayable, refundAmount);
        
        return SettlementCalculationResult.builder()
                .workDays(workDays)
                .totalDays(totalDays)
                .unworkedDays(unworkedDays)
                .dailyRate(dailyRate)
                .baseEarnings(baseEarnings)
                .penalty(penalty)
                .employeeFinal(employeeFinal)
                .commission(commission)
                .employerPayable(employerPayable)
                .refundAmount(refundAmount)
                .defaultingParty(defaultingParty)
                .calculationType(calculationType)
                .build();
    }

    /**
     * @description 保存结算记录
     * @author CyberCaelum
     * @date 2026/3/21
     * @param order 订单信息
     * @param result 结算计算结果
     * @param refundNo 退款单号（可为null）
     **/
    private void saveSettlementRecord(Order order, SettlementCalculationResult result, String refundNo) {
        Long orderId = order.getId();
        
        // 查询是否已有结算记录
        Settlement existingSettlement = settlementMapper.selectByOrderId(orderId);
        
        if (existingSettlement != null) {
            // 更新现有结算记录
            existingSettlement.setTotalDays(result.getWorkDays());
            existingSettlement.setDailyRate(result.getDailyRate());
            existingSettlement.setTotalAmount(result.getBaseEarnings());
            existingSettlement.setFinalAmount(result.getEmployerPayable());
            existingSettlement.setPenaltyDeduction(result.getPenalty());
            existingSettlement.setDefaultingParty(result.getDefaultingParty());
            existingSettlement.setStatus(SettlementStatusConstant.SETTLED);
            existingSettlement.setSettlementTime(LocalDateTime.now());
            existingSettlement.setOrderNumber(order.getOrderNumber());
            if (refundNo != null) {
                existingSettlement.setRefundNumber(refundNo);
            }
            settlementMapper.update(existingSettlement);
            log.info("更新结算记录完成，订单ID: {}，类型: {}", orderId, result.getCalculationType());
        } else {
            // 创建新的结算记录
            Settlement settlement = Settlement.builder()
                    .orderId(orderId)
                    .totalDays(result.getWorkDays())
                    .dailyRate(result.getDailyRate())
                    .totalAmount(result.getBaseEarnings())
                    .finalAmount(result.getEmployerPayable())
                    .penaltyDeduction(result.getPenalty())
                    .defaultingParty(result.getDefaultingParty())
                    .status(SettlementStatusConstant.SETTLED)
                    .settlementTime(LocalDateTime.now())
                    .orderNumber(order.getOrderNumber())
                    .refundNumber(refundNo)
                    .createTime(LocalDateTime.now())
                    .build();
            settlementMapper.insert(settlement);
            log.info("创建结算记录完成，订单ID: {}，类型: {}", orderId, result.getCalculationType());
        }
        
        // TODO: 调用支付系统，将 employeeFinal 打给雇员
        // transferToEmployee(order.getEmployeeId(), result.getEmployeeFinal());
    }

    /**
     * @description 提交订单
     * @author CyberCaelum
     * @date 上午9:14 2026/3/12
     * @param ordersSubmitDTO 订单信息
     * @return org.cybercaelum.household_management.pojo.vo.OrderSubmitVO
     **/
    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        //获取招募id
        Long recruitmentId = ordersSubmitDTO.getRecruitmentId();
        //查询招募
        Recruitment recruitment = recruitmentMapper.selectRecruitmentById(recruitmentId);
        //如果没有报错
        if (recruitment == null) {
            throw new RecruitmentNotFoundException("招募不存在");
        }
        //控制同一招募不能同时有多个进行中的订单
        List<Order> orders = orderMapper.getOrderByRecruitmentId(recruitmentId);
        if (orders != null && !orders.isEmpty()) {
            throw new OrderStatusErrorException("订单已存在，请结束上一个订单");
        }
        //薪水在最大和最小之间
        if (ordersSubmitDTO.getPrice().compareTo(recruitment.getMineSalary())<0
                || ordersSubmitDTO.getPrice().compareTo(recruitment.getMaxSalary())>0){
            throw new OrderPriceException("薪资错误");
        }
        Order order = Order.builder()
                .price(ordersSubmitDTO.getPrice())//价格
                .orderTime(LocalDateTime.now())//下单时间
                .recruitmentId(recruitmentId)//招募id
                .startTime(ordersSubmitDTO.getStartTime())//开始时间
                .endTime(ordersSubmitDTO.getEndTime())//结束时间
                .employerId(recruitment.getUserId())//雇佣者id，即发帖人id
                .days(ordersSubmitDTO.getDays())//工作天数
                .orderNumber(UUID.randomUUID().toString().replace("-", ""))//订单号
                .cancelType(CancelApplicationStatusConstant.TYPE_NOT_CANCELLED)//设置状态为未取消
                .build();
        //复制地址
        BeanUtils.copyProperties(recruitment,order);
        //设置订单状态，待付款
        order.setStatus(OrderStatusConstant.PENDING_PAYMENT);
        //设置支付状态，未支付
        order.setPayStatus(PayStatusConstant.UN_PAID);
        //计算托管金额 = 天数*日薪
        BigDecimal heldAmount = ordersSubmitDTO.getPrice().multiply(new BigDecimal(ordersSubmitDTO.getDays()));
        //托管金额
        order.setHeldAmount(heldAmount);
        //应付总价 = 托管金额 + 平台佣金 = 托管金额 * (1 + 平台佣金比例)
        BigDecimal total = heldAmount.add(heldAmount.multiply(OrderRateConstant.PLATFORM_COMMISSION_RATE));
        order.setTotal(total);
        //存入数据库
        orderMapper.insertOrder(order);

        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();
        BeanUtils.copyProperties(order,orderSubmitVO);
        //设置订单金额
        orderSubmitVO.setOrderAmount(total);
        //发送延迟消息，控制订单过期
        OrderTimeoutMessage orderTimeoutMessage = new OrderTimeoutMessage();
        orderTimeoutMessage.setId(order.getId());
        orderTimeoutMessage.setUserId(BaseContext.getUserId());
        orderTimeoutMessage.setCreateTime(order.getOrderTime());
        orderTimeoutMessage.setAmount(total);
        sendOrderTimeoutMessage(orderTimeoutMessage);
        //返回数据
        return orderSubmitVO;
    }

    /**
     * @description 订单超时消息
     * @author CyberCaelum
     * @date 上午9:22 2026/3/18
     * @param message 订单消息
     **/
    public void sendOrderTimeoutMessage(OrderTimeoutMessage message){
        //计算延迟投递时间
        long deliveryTimestamp = System.currentTimeMillis() + RocketMQConstant.ORDER_TIMEOUT_DEFAULT;
        try {
            org.springframework.messaging.Message<?> msg = MessageBuilder
                    .withPayload(JSON.toJSONString(message).getBytes(StandardCharsets.UTF_8))
                    .setHeader("DELIVERY_TIMESTAMP", deliveryTimestamp)
                    .build();
            rocketMQClientTemplate.send(RocketMQConstant.ORDER_TIMEOUT_TOPIC+":"+RocketMQConstant.ORDER_CANCEL_TAG,msg);
        } catch (Exception e) {
            throw new RuntimeException("消息发送错误", e);
        }
    }

    /**
     * @description 生成并插入每日确认记录
     * @author CyberCaelum
     * @date 上午10:06 2026/3/16
     * @param order 订单信息
     **/
    private void generateDailyConfirmations(Order order) {
        List<DailyConfirmation> confirmations = new ArrayList<>();
        LocalDate startDate = order.getStartTime();
        LocalDate endDate = order.getEndTime();
        LocalDate currentDate = startDate;
        //循环生成每日记录
        while (!currentDate.isAfter(endDate)) {
            DailyConfirmation confirmation = DailyConfirmation.builder()
                    .orderId(order.getId())
                    .serviceDate(currentDate)
                    .status(DailyConfirmationStatusConstant.PENDING)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            confirmations.add(confirmation);
            currentDate = currentDate.plusDays(1);
        }
        
        if (!confirmations.isEmpty()) {
            dailyConfirmationMapper.batchInsert(confirmations);
        }
    }

    /**
     * @description 生成订单相关的微信支付二维码
     * @author CyberCaelum
     * @date 下午4:05 2026/3/17
     * @param ordersPaymentDTO 订单信息
     * @return org.cybercaelum.household_management.pojo.vo.OrderPaymentVO
     **/
    @Override
    public String nativeOrder(OrdersPaymentDTO ordersPaymentDTO) {
        //获取order信息
        Order order = orderMapper.getOrderById(ordersPaymentDTO.getOrderId());
        //判断订单是否存在
        if (order == null) {
            throw new OrderNotFoundException("订单不存在");
        }
        //订单和订单id是否匹配
        if (!order.getOrderNumber().equals(ordersPaymentDTO.getOrderNumber())) {
            throw new OrderNotFoundException("订单错误");
        }
        String codeUrl = wechatPayUtil.createNativeOrder(
                ordersPaymentDTO.getOrderNumber(),
                order.getTotal(),
                "家政服务订单，扫码支付"
        );
        
        // 发送支付超时消息（用于回调保底）
        sendPayTimeoutMessage(order.getId(), order.getOrderNumber());
        
        return codeUrl;
    }
    
    /**
     * @description 发送支付超时消息（用于回调保底）
     * @author CyberCaelum
     * @date 2026/3/20
     * @param orderId 订单id
     * @param orderNumber 订单号
     **/
    private void sendPayTimeoutMessage(Long orderId, String orderNumber){
        //计算延迟投递时间（5分钟后）
        long deliveryTimestamp = System.currentTimeMillis() + RocketMQConstant.PAY_TIMEOUT_DEFAULT;
        try {
            PayTimeoutMessage message = new PayTimeoutMessage();
            message.setOrderId(orderId);
            message.setOrderNumber(orderNumber);
            message.setCreateTime(LocalDateTime.now());
            
            org.springframework.messaging.Message<?> msg = MessageBuilder
                    .withPayload(JSON.toJSONString(message).getBytes(StandardCharsets.UTF_8))
                    .setHeader("DELIVERY_TIMESTAMP", deliveryTimestamp)
                    .build();
            rocketMQClientTemplate.send(RocketMQConstant.PAY_TIMEOUT_TOPIC + ":" + RocketMQConstant.PAY_TIMEOUT_TAG, msg);
            log.info("支付超时消息已发送，订单ID: {}，订单号: {}", orderId, orderNumber);
        } catch (Exception e) {
            log.error("支付超时消息发送失败，订单ID: {}", orderId, e);
        }
    }

    /**
     * @description 退款（根据平台裁决结果执行退款，实际状态更新在退款回调中处理）
     * 退款金额统一从结算记录中读取，避免重复计算导致不一致
     * @author CyberCaelum
     * @date 上午10:28 2026/3/18
     * @param orderId 订单id
     **/
    @Override
    @Transactional
    public void refund(Long orderId){
        //查找订单是否存在
        Order order = orderMapper.getOrderById(orderId);
        if (order == null || order.getOrderNumber() == null){
            throw new OrderNotFoundException("订单不存在");
        }
        
        //幂等性检查：已退款订单不再处理
        if (PayStatusConstant.REFUNDED.equals(order.getPayStatus())) {
            log.info("订单已退款，无需重复处理，订单ID: {}", orderId);
            return;
        }
        
        //检查订单状态,待付款订单不能退款
        if (PayStatusConstant.UN_PAID.equals(order.getPayStatus()) ||
            OrderStatusConstant.PENDING_PAYMENT.equals(order.getStatus())){
            throw new OrderStatusErrorException("订单未支付");
        }
        
        //通过微信获得订单支付状态
        WxPayOrderQueryResult wxPayOrderQueryResult = wechatPayUtil.queryOrder(order.getOrderNumber());
        if (!"SUCCESS".equals(wxPayOrderQueryResult.getTradeState())){
            throw new OrderStatusErrorException("订单未支付");
        }
        
        //查询争议处理结果
        DisputeResolution disputeResolution = disputeResolutionMapper.selectDisputeResolutionByOrderId(orderId, DisputeResolutionConstant.CANCEL_APPLY);
        //判断裁决结果是否存在
        if (disputeResolution == null){
            throw new DisputeResolutionIsNullException("没有裁决结果");
        }
        
        //判断平台决定如何退款
        Integer decision = disputeResolution.getDecision();
        
        //拒绝取消，不退款
        if (DisputeResolutionConstant.REJECT.equals(decision)){
            log.info("平台裁决拒绝取消，不予退款，订单ID: {}", orderId);
            return;
        }
        
        //生成退款订单号
        String refundNumber = String.valueOf(System.currentTimeMillis()) + order.getRecruitmentId();
        
        //同意取消，全额退款（托管金额+平台佣金全部退给雇主）
        if (DisputeResolutionConstant.AGREE.equals(decision)){
            //调用微信退款接口，实际状态更新在回调中处理
            wechatPayUtil.refund(order.getOrderNumber(), refundNumber, order.getTotal(),
                    order.getTotal(), "平台同意全额退款");
            
            //发送延迟消息，用于处理退款超时
            sendRefundTimeoutMessage(orderId, refundNumber);
            
            log.info("全额退款申请已提交，订单ID: {}，退款单号: {}", orderId, refundNumber);
        }
        
        //部分结算
        if (DisputeResolutionConstant.PARTIAL_SETTLEMENT.equals(decision)){
            //从结算记录中读取退款金额，避免重复计算
            Settlement settlement = settlementMapper.selectByOrderId(orderId);
            if (settlement == null) {
                throw new OrderStatusErrorException("结算记录不存在，无法执行退款");
            }
            
            //退款金额 = 订单总额 - 结算金额（雇主实际应支付金额）
            BigDecimal refundAmount = order.getTotal().subtract(settlement.getFinalAmount());
            if (refundAmount.compareTo(BigDecimal.ZERO) < 0) {
                refundAmount = BigDecimal.ZERO;
            }
            
            //获取违约方用于日志记录
            Integer defaultingParty = disputeResolution.getDefaultingParty();
            String refundReason;
            if (defaultingParty == null) {
                refundReason = "平台同意部分退款";
            } else if (DisputeResolutionConstant.EMPLOYER_DEFAULTING.equals(defaultingParty)) {
                refundReason = "雇主违约，扣除违约金后部分退款";
            } else if (DisputeResolutionConstant.EMPLOYEE_DEFAULTING.equals(defaultingParty)) {
                refundReason = "雇员违约，扣除违约金后部分退款";
            } else {
                refundReason = "平台裁决部分退款";
            }
            
            wechatPayUtil.refund(order.getOrderNumber(), refundNumber, refundAmount,
                    order.getTotal(), refundReason);
            
            //发送延迟消息，用于处理退款超时
            sendRefundTimeoutMessage(orderId, refundNumber);
            
            log.info("部分退款申请已提交，订单ID: {}，退款单号: {}，退款金额: {}，违约方: {}", 
                    orderId, refundNumber, refundAmount, defaultingParty);
        }
    }
    
    /**
     * @description 发送退款超时消息
     * @author CyberCaelum
     * @date 2026/3/20
     * @param orderId 订单id
     * @param refundNumber 退款单号
     **/
    private void sendRefundTimeoutMessage(Long orderId, String refundNumber){
        //计算延迟投递时间（5分钟后）
        long deliveryTimestamp = System.currentTimeMillis() + RocketMQConstant.REFUND_TIMEOUT_DEFAULT;
        try {
            RefundTimeoutMessage message = new RefundTimeoutMessage();
            message.setOrderId(orderId);
            message.setRefundNumber(refundNumber);
            message.setCreateTime(LocalDateTime.now());
            
            org.springframework.messaging.Message<?> msg = MessageBuilder
                    .withPayload(JSON.toJSONString(message).getBytes(StandardCharsets.UTF_8))
                    .setHeader("DELIVERY_TIMESTAMP", deliveryTimestamp)
                    .build();
            rocketMQClientTemplate.send(RocketMQConstant.REFUND_TIMEOUT_TOPIC + ":" + RocketMQConstant.REFUND_TIMEOUT_TAG, msg);
        } catch (Exception e) {
            log.error("退款超时消息发送失败，订单ID: {}", orderId, e);
        }
    }

    //TODO 给雇员打款

    /**
     * @description 支付成功修改订单状态，生成每日确定
     * @author CyberCaelum
     * @date 上午10:32 2026/3/13
     * @param orderNumber 订单号
     * @param payMethod 支付方式
     * @param totalFee 微信支付金额（分）
     **/
    @Override
    @Transactional
    public void paySuccess(String orderNumber, Integer payMethod, Integer totalFee) {
        //根据订单号查询订单
        Order order = orderMapper.getOrderByNumber(orderNumber);
        
        //订单不存在
        if (order == null) {
            log.error("支付回调处理失败：订单不存在，订单号: {}", orderNumber);
            throw new OrderNotFoundException("订单不存在");
        }
        
        //幂等性检查：如果订单已经支付，直接返回（防止重复处理）
        if (PayStatusConstant.PAID.equals(order.getPayStatus())) {
            log.info("订单已支付，无需重复处理，订单号: {}", orderNumber);
            return;
        }
        
        //校验订单状态：只有待支付的订单才能处理支付成功
        if (!OrderStatusConstant.PENDING_PAYMENT.equals(order.getStatus())) {
            log.warn("订单状态异常，无法处理支付，订单号: {}，当前状态: {}", orderNumber, order.getStatus());
            throw new OrderStatusErrorException("订单状态异常，无法处理支付");
        }
        
        // 支付金额一致性校验：将分转换为元进行比较
        if (totalFee != null) {
            BigDecimal wxPayAmount = new BigDecimal(totalFee).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            if (wxPayAmount.compareTo(order.getTotal()) != 0) {
                log.error("支付金额不一致，订单号: {}，订单金额: {}，微信支付金额: {}", 
                        orderNumber, order.getTotal(), wxPayAmount);
                throw new OrderStatusErrorException("支付金额与订单金额不一致");
            }
        }
        
        //修改订单状态和信息（使用乐观锁防止并发）
        Order payedOrder = Order.builder()
                .id(order.getId())//主键
                .payStatus(PayStatusConstant.PAID)//已支付
                .payStatusCondition(PayStatusConstant.UN_PAID)//乐观锁条件：只有未支付状态才能更新
                .paymentTime(LocalDateTime.now())//支付时间
                .status(OrderStatusConstant.TO_BE_CONFIRMED)//从未支付变为待确认
                .payMethod(payMethod)//支付方式
                .build();
        int affectedRows = orderMapper.updateOrderWithOptimisticLock(payedOrder);
        
        // 乐观锁校验：如果没有更新成功，说明已被其他线程处理
        if (affectedRows == 0) {
            log.warn("订单支付状态已被其他线程处理，订单号: {}", orderNumber);
            return;
        }
        
        // 设置招募信息为隐藏
        recruitmentService.updateRecruitmentStatus(RecruitmentStatusConstant.HIDDEN,order.getId());
        //生成每日确认记录
        generateDailyConfirmations(order);
        log.info("订单支付成功处理完成，订单号: {}，支付方式: {}", orderNumber, payMethod);
    }

    /**
     * @description 查看历史订单
     * @author CyberCaelum
     * @date 上午10:13 2026/3/13
     * @param page 页数
     * @param pageSize 页面大小
     * @param status 订单状态
     * @return org.cybercaelum.household_management.pojo.entity.PageResult
     **/
    @Override
    public PageResult history(Integer page, Integer pageSize, Integer status) {
        PageHelper.startPage(page, pageSize);
        Page<Order> orders = orderMapper.history(BaseContext.getUserId(), status);
        List<OrderVO> list = new ArrayList<>();
        if (orders != null && !orders.isEmpty()) {
            // 遍历订单
            for (Order order : orders) {
                OrderVO orderVO = new OrderVO();
                // 将订单数据复制到orderVO中
                BeanUtils.copyProperties(order, orderVO);
                list.add(orderVO);
            }
        }
        return new PageResult(orders.getTotal(), list);
    }

    /**
     * @description 查看订单详细信息
     * @author CyberCaelum
     * @date 上午11:00 2026/3/13
     * @param id 订单主键
     * @return org.cybercaelum.household_management.pojo.vo.OrderVO
     **/
    @Override
    public OrderVO detail(Long id) {
        Order order = orderMapper.getOrderById(id);
        if (order == null) {
            throw new OrderNotFoundException("订单不存在");
        }
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order,orderVO);
        return orderVO;
    }

    /**
     * @description 接单（被雇者确认接单）
     * @author CyberCaelum
     * @date 上午10:34 2026/3/16
     * @param ordersConfirmDTO 订单信息
     **/
    @Override
    @Transactional
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Long orderId = ordersConfirmDTO.getId();
        Order order = orderMapper.getOrderById(orderId);
        if (order == null) {
            throw new OrderNotFoundException("订单不存在");
        }
        
        // 验证订单状态
        if (!OrderStatusConstant.TO_BE_CONFIRMED.equals(order.getStatus())) {
            throw new OrderStatusErrorException("订单状态错误，无法接单");
        }
        Long userId = BaseContext.getUserId();
        // 更新订单状态为已接单，设置被雇者ID
        Order updateOrder = Order.builder()
                .id(orderId)
                .status(OrderStatusConstant.CONFIRMED)
                .employeeId(userId)
                .build();
        orderMapper.updateOrder(updateOrder);
    }

    /**
     * @description 拒单（被雇者拒绝接单）
     * @author CyberCaelum
     * @date 2026/3/16
     * @param ordersRejectionDTO 拒单信息
     **/
    @Override
    @Transactional
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        Long orderId = ordersRejectionDTO.getId();
        Order order = orderMapper.getOrderById(orderId);
        if (order == null) {
            throw new OrderNotFoundException("订单不存在");
        }
        
        // 验证订单状态
        if (!OrderStatusConstant.TO_BE_CONFIRMED.equals(order.getStatus())) {
            throw new OrderStatusErrorException("订单状态错误，无法拒单");
        }
        
        // 通过微信获得订单支付状态
        WxPayOrderQueryResult wxPayOrderQueryResult = wechatPayUtil.queryOrder(order.getOrderNumber());
        if (!"SUCCESS".equals(wxPayOrderQueryResult.getTradeState())){
            throw new OrderStatusErrorException("订单未支付");
        }
        
        // 创建退款单号
        String refundNo = String.valueOf(System.currentTimeMillis()) + order.getRecruitmentId();
        
        // 调用微信退款接口，全额退款
        wechatPayUtil.refund(order.getOrderNumber(), refundNo, order.getTotal(), order.getTotal(), "家政人员拒绝订单");
        
        // 发送退款超时消息，用于回调保底
        sendRefundTimeoutMessage(orderId, refundNo);
        
        // 更新订单状态为已取消，记录拒单原因
        Order updateOrder = Order.builder()
                .id(orderId)
                .status(OrderStatusConstant.CANCELLED)
                .rejectionReason(ordersRejectionDTO.getRejectionReason())
                .rejectionTime(LocalDateTime.now())
                .refundTime(LocalDateTime.now())
                .refundNumber(refundNo)
                .payStatus(PayStatusConstant.REFUNDING) // 设置退款中状态
                .cancelType(CancelApplicationStatusConstant.TYPE_WORKER_FORCE) // 家政人员强制取消
                .build();
        orderMapper.updateOrder(updateOrder);
        
        log.info("拒单退款申请已提交，订单ID: {}，退款单号: {}，退款金额: {}", orderId, refundNo, order.getTotal());
    }

    /**
     * @description 平台取消订单
     * @author CyberCaelum
     * @date 2026/3/16
     * @param ordersCancelDTO 订单取消信息
     **/
    @Override
    @Transactional
    public void adminCancel(OrdersCancelDTO ordersCancelDTO) {
        Long orderId = ordersCancelDTO.getId();
        Order order = orderMapper.getOrderById(orderId);
        if (order == null) {
            throw new OrderNotFoundException("订单不存在");
        }
        
        // 判断订单是否已付款
        if (PayStatusConstant.PAID.equals(order.getPayStatus())) {
            // 已支付，需要执行退款
            // 通过微信查询订单支付状态，确保订单已支付
            WxPayOrderQueryResult wxPayOrderQueryResult = wechatPayUtil.queryOrder(order.getOrderNumber());
            if (!"SUCCESS".equals(wxPayOrderQueryResult.getTradeState())) {
                throw new OrderStatusErrorException("订单未支付或支付状态异常");
            }
            
            // 创建退款单号
            String refundNo = String.valueOf(System.currentTimeMillis()) + order.getRecruitmentId();
            
            // 调用微信退款接口，全额退款
            wechatPayUtil.refund(order.getOrderNumber(), refundNo, order.getTotal(), order.getTotal(), "平台取消订单，全额退款");
            
            // 发送退款超时消息，用于回调保底
            sendRefundTimeoutMessage(orderId, refundNo);
            
            // 更新订单状态为已取消，设置退款中状态
            Order updateOrder = Order.builder()
                    .id(orderId)
                    .status(OrderStatusConstant.CANCELLED)
                    .cancelReason(ordersCancelDTO.getCancelReason())
                    .cancelTime(LocalDateTime.now())
                    .cancelType(CancelApplicationStatusConstant.TYPE_PLATFORM_FORCE) // 平台取消
                    .payStatus(PayStatusConstant.REFUNDING) // 设置退款中状态
                    .refundNumber(refundNo)
                    .build();
            orderMapper.updateOrder(updateOrder);
            
            log.info("平台取消订单，已申请全额退款，订单ID: {}，退款单号: {}，退款金额: {}", 
                    orderId, refundNo, order.getTotal());
        } else {
            // 未支付，直接取消订单
            Order updateOrder = Order.builder()
                    .id(orderId)
                    .status(OrderStatusConstant.CANCELLED)
                    .cancelReason(ordersCancelDTO.getCancelReason())
                    .cancelTime(LocalDateTime.now())
                    .cancelType(CancelApplicationStatusConstant.TYPE_PLATFORM_FORCE) // 平台取消
                    .build();
            orderMapper.updateOrder(updateOrder);
            
            log.info("平台取消订单，订单未支付，直接取消，订单ID: {}", orderId);
        }
    }

    /**
     * @description 直接取消订单（用于待付款状态的订单）
     * @author CyberCaelum
     * @date 2026/3/21
     * @param orderId 订单id
     * @param reason 取消原因
     **/
    @Transactional
    public void directCancel(Long orderId, String reason) {
        Order order = orderMapper.getOrderById(orderId);
        if (order == null) {
            throw new OrderNotFoundException("订单不存在");
        }
        
        // 只能取消待付款订单
        if (!OrderStatusConstant.PENDING_PAYMENT.equals(order.getStatus())) {
            throw new OrderStatusErrorException("只有待付款订单可以直接取消");
        }
        
        // 权限校验：仅允许雇主或雇员本人取消
        Long userId = BaseContext.getUserId();
        if (!userId.equals(order.getEmployerId()) && !userId.equals(order.getEmployeeId())) {
            throw new PermissionDeniedException("无权操作此订单");
        }
        
        // 根据当前用户角色确定取消类型
        Integer cancelType;
        if (userId.equals(order.getEmployerId())) {
            cancelType = CancelApplicationStatusConstant.TYPE_EMPLOYER_FORCE;
        } else {
            // 雇员取消
            cancelType = CancelApplicationStatusConstant.TYPE_WORKER_FORCE;
        }
        
        // 更新订单状态为已取消
        Order updateOrder = Order.builder()
                .id(orderId)
                .status(OrderStatusConstant.CANCELLED)
                .cancelReason(reason)
                .cancelTime(LocalDateTime.now())
                .cancelType(cancelType)
                .build();
        orderMapper.updateOrder(updateOrder);
        
        log.info("待付款订单直接取消成功，订单ID: {}，原因: {}", orderId, reason);
    }

    /**
     * @description 标记订单为进行中
     * @author CyberCaelum
     * @date 2026/3/16
     * @param id 主键
     **/
    @Override
    @Transactional
    public void delivery(Long id) {
        Order order = orderMapper.getOrderById(id);
        if (order == null) {
            throw new OrderNotFoundException("订单不存在");
        }
        
        // 验证订单状态
        if (!OrderStatusConstant.CONFIRMED.equals(order.getStatus())) {
            throw new OrderStatusErrorException("订单状态错误");
        }
        
        // 更新订单状态为进行中
        Order updateOrder = Order.builder()
                .id(id)
                .status(OrderStatusConstant.IN_PROGRESS)
                .build();
        orderMapper.updateOrder(updateOrder);
    }

    /**
     * @description 完成订单
     * @author CyberCaelum
     * @date 2026/3/16
     * @param id 订单id
     **/
    @Override
    @Transactional
    public void complete(Long id) {
        Order order = orderMapper.getOrderById(id);
        if (order == null) {
            throw new OrderNotFoundException("订单不存在");
        }
        
        // 验证订单状态
        if (!OrderStatusConstant.IN_PROGRESS.equals(order.getStatus())) {
            throw new OrderStatusErrorException("订单状态错误，无法完成");
        }
        
        // 校验是否所有服务日都已确认
        int unconfirmedDays = orderMapper.countUnconfirmedDays(id);
        if (unconfirmedDays > 0) {
            throw new OrderStatusErrorException("还有 " + unconfirmedDays + " 天服务未确认，无法完成订单");
        }
        
        // 更新订单状态为已完成
        Order updateOrder = Order.builder()
                .id(id)
                .status(OrderStatusConstant.COMPLETED)
                .build();
        orderMapper.updateOrder(updateOrder);
        
        // 执行结算
        settleOrder(id, null);
    }

    /**
     * @description 家政人员每日服务完成确认
     * @author CyberCaelum
     * @date 2026/3/16
     * @param orderId 订单id
     * @param serviceDate 服务日期
     **/
    @Override
    @Transactional
    public void workerDailyConfirm(Long orderId, LocalDate serviceDate) {
        Order order = orderMapper.getOrderById(orderId);
        if (order == null) {
            throw new OrderNotFoundException("订单不存在");
        }
        
        // 验证权限（必须是家政人员）
        Long userId = BaseContext.getUserId();
        if (!userId.equals(order.getEmployeeId())) {
            throw new PermissionDeniedException("无权操作此订单");
        }
        
        // 校验服务日期在订单有效期内
        if (serviceDate.isBefore(order.getStartTime()) || serviceDate.isAfter(order.getEndTime())) {
            throw new OrderParamException("服务日期不在订单服务范围内");
        }
        
        // 校验只能确认当天或之前的服务（防止提前确认）
        if (serviceDate.isAfter(LocalDate.now())) {
            throw new OrderParamException("不能提前确认未来日期的服务");
        }
        
        // 查询当日确认记录
        DailyConfirmation confirmation = dailyConfirmationMapper.selectByOrderIdAndDate(orderId, serviceDate);
        if (confirmation == null) {
            throw new OrderStatusErrorException("该日期的服务记录不存在");
        }
        
        // 更新家政人员确认时间
        DailyConfirmation updateConfirmation = DailyConfirmation.builder()
                .id(confirmation.getId())
                .workerConfirmTime(LocalDateTime.now())
                .build();
        dailyConfirmationMapper.update(updateConfirmation);
    }

    /**
     * @description 雇主确认每日服务
     * @author CyberCaelum
     * @date 2026/3/16
     * @param confirmationId 确认id
     **/
    @Override
    @Transactional
    public void employerDailyConfirm(Long confirmationId) {
        DailyConfirmation confirmation = dailyConfirmationMapper.selectById(confirmationId);
        if (confirmation == null) {
            throw new OrderNotFoundException("确认记录不存在");
        }
        
        // 校验家政人员已先确认
        if (confirmation.getWorkerConfirmTime() == null) {
            throw new OrderStatusErrorException("家政人员尚未确认，雇主无法确认");
        }
        
        Order order = orderMapper.getOrderById(confirmation.getOrderId());
        if (order == null) {
            throw new OrderNotFoundException("订单不存在");
        }
        
        // 验证权限（必须是雇主）
        Long userId = BaseContext.getUserId();
        if (!userId.equals(order.getEmployerId())) {
            throw new PermissionDeniedException("无权操作此订单");
        }
        
        // 更新确认状态为雇主已确认
        DailyConfirmation updateConfirmation = DailyConfirmation.builder()
                .id(confirmationId)
                .status(DailyConfirmationStatusConstant.EMPLOYER_CONFIRMED)
                .employerConfirmTime(LocalDateTime.now())
                .build();
        dailyConfirmationMapper.update(updateConfirmation);
    }

    /**
     * @description 雇主对每日服务提出争议
     * @author CyberCaelum
     * @date 2026/3/16
     * @param confirmationId 确认id
     * @param reason 原因
     **/
    @Override
    @Transactional
    public void employerDisputeDaily(Long confirmationId, String reason) {
        DailyConfirmation confirmation = dailyConfirmationMapper.selectById(confirmationId);
        if (confirmation == null) {
            throw new OrderNotFoundException("确认记录不存在");
        }
        
        Order order = orderMapper.getOrderById(confirmation.getOrderId());
        if (order == null) {
            throw new OrderNotFoundException("订单不存在");
        }
        
        // 验证权限（必须是雇主）
        Long userId = BaseContext.getUserId();
        if (!userId.equals(order.getEmployerId())) {
            throw new PermissionDeniedException("无权操作此订单");
        }
        
        // 更新确认状态为争议
        DailyConfirmation updateConfirmation = DailyConfirmation.builder()
                .id(confirmationId)
                .status(DailyConfirmationStatusConstant.EMPLOYER_REJECTED)
                .disputeReason(reason)
                .build();
        //TODO 确认为争议后怎么处理？
        //自动通知平台介入，
        dailyConfirmationMapper.update(updateConfirmation);
    }

    /**
     * @description 发起取消申请
     * @author CyberCaelum
     * @date 上午10:11 2026/3/16
     * @param orderId 订单id
     * @param cancelType 取消类型
     * @param reason 原因
     **/
    @Override
    @Transactional
    public void applyCancel(Long orderId, Integer cancelType, String reason) {
        Order order = orderMapper.getOrderById(orderId);
        if (order == null) {
            throw new OrderNotFoundException("订单不存在");
        }
        
        // 验证订单状态（只能取消进行中,待被确定，待付款，已接单的订单）
        if (!OrderStatusConstant.IN_PROGRESS.equals(order.getStatus()) && //进行中
            !OrderStatusConstant.PENDING_PAYMENT.equals(order.getStatus()) && //待付款
            !OrderStatusConstant.TO_BE_CONFIRMED.equals(order.getStatus()) && //待被确认
            !OrderStatusConstant.CONFIRMED.equals(order.getStatus()) ) { //已接单
            throw new OrderStatusErrorException("当前订单状态无法取消");
        }
        
        // 待付款订单直接取消，不走申请流程
        if (OrderStatusConstant.PENDING_PAYMENT.equals(order.getStatus())) {
            directCancel(orderId, reason);
            return;
        }
        
        Long userId = BaseContext.getUserId();
        Integer role;
        
        // 确定申请人角色
        if (userId.equals(order.getEmployerId())) {
            role = CancelApplicationStatusConstant.ROLE_EMPLOYER;
        } else if (userId.equals(order.getEmployeeId())) {
            role = CancelApplicationStatusConstant.ROLE_WORKER;
        } else {
            throw new PermissionDeniedException("无权操作此订单");
        }
        
        // 检查是否已有有效申请
        CancelApplication existingApp = cancelApplicationMapper.selectActiveByOrderId(orderId);
        if (existingApp != null) {
            throw new OrderStatusErrorException("该订单已有待处理的取消申请");
        }
        
        // 创建取消申请
        CancelApplication application = CancelApplication.builder()
                .orderId(orderId)
                .applicantId(userId)
                .applicantRole(role)
                .cancelType(cancelType)
                .reason(reason)
                .status(CancelApplicationStatusConstant.PENDING_CONFIRM)
                .expireTime(LocalDateTime.now().plusHours(24)) // 24小时超时
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        
        cancelApplicationMapper.insert(application);
    }

    /**
     * @description 响应取消申请（同意或拒绝）
     * @author CyberCaelum
     * @date 上午10:47 2026/3/16
     * @param applicationId 申请id
     * @param agree 是否同意
     **/
    @Override
    @Transactional
    public void respondCancelApplication(Long applicationId, Boolean agree) {
        //获取申请信息
        CancelApplication application = cancelApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new OrderNotFoundException("取消申请不存在");
        }
        
        // 验证状态
        if (!CancelApplicationStatusConstant.PENDING_CONFIRM.equals(application.getStatus())) {
            throw new OrderStatusErrorException("该申请已处理或已超时");
        }

        //验证订单
        Order order = orderMapper.getOrderById(application.getOrderId());
        if (order == null) {
            throw new OrderNotFoundException("订单不存在");
        }
        
        // 验证权限（必须是对方）
        Long userId = BaseContext.getUserId();
        boolean isApplicantEmployer = CancelApplicationStatusConstant.ROLE_EMPLOYER.equals(application.getApplicantRole());
        
        if (isApplicantEmployer && !userId.equals(order.getEmployeeId())) {
            throw new PermissionDeniedException("无权处理此申请");
        }
        if (!isApplicantEmployer && !userId.equals(order.getEmployerId())) {
            throw new PermissionDeniedException("无权处理此申请");
        }
        
        if (agree) {
            // 同意取消
            application.setStatus(CancelApplicationStatusConstant.CONFIRMED_AGREE);
            application.setConfirmUserId(userId);
            application.setConfirmTime(LocalDateTime.now());
            cancelApplicationMapper.update(application);
            
            // 执行结算
            settleOrder(order.getId(), application.getId());
            
            // 更新订单状态为已取消
            Order updateOrder = Order.builder()
                    .id(order.getId())
                    .status(OrderStatusConstant.CANCELLED)
                    .cancelType(getCancelTypeFromApplication(application.getCancelType()))
                    .cancelTime(LocalDateTime.now())
                    .build();
            orderMapper.updateOrder(updateOrder);
        } else {
            // 拒绝取消
            //TODO 平台介入
            application.setStatus(CancelApplicationStatusConstant.CONFIRMED_REJECT);
            application.setConfirmUserId(userId);
            application.setConfirmTime(LocalDateTime.now());
            cancelApplicationMapper.update(application);
        }
    }

    /**
     * @description 转换取消类型
     * @author CyberCaelum
     * @date 上午11:01 2026/3/16
     * @param cancelType 类型
     * @return int
     **/
    private int getCancelTypeFromApplication(Integer cancelType) {
        return switch (cancelType) {
            case 1 -> 1; // 协商一致取消
            case 2 -> 2; // 雇主强制取消
            case 3 -> 3; // 家政人员强制取消
            default -> 0;
        };
    }

    /**
     * @description 平台裁决取消申请
     * @author CyberCaelum
     * @date 上午11:01 2026/3/16
     * @param applicationId 申请id
     * @param decision 裁决结果：1同意取消，2拒绝取消，3部分结算
     * @param defaultingParty 违约方：1-雇主，2-雇员，null-无违约方
     * @param note 平台备注
     **/
    @Override
    @Transactional
    public void platformDecideCancelApplication(Long applicationId, Integer decision, Integer defaultingParty, String note) {
        //获取申请信息
        CancelApplication application = cancelApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new OrderNotFoundException("取消申请不存在");
        }
        
        // 验证状态
        if (!CancelApplicationStatusConstant.PLATFORM_PROCESSING.equals(application.getStatus())) {
            throw new OrderStatusErrorException("该申请不在平台处理中状态");
        }
        
        Order order = orderMapper.getOrderById(application.getOrderId());
        if (order == null) {
            throw new OrderNotFoundException("订单不存在");
        }
        
        // 更新申请状态
        application.setStatus(CancelApplicationStatusConstant.PLATFORM_DECIDED);
        application.setPlatformDecision(decision);
        application.setPlatformOperator(BaseContext.getUserId());
        application.setPlatformNote(note);
        cancelApplicationMapper.update(application);
        
        // 创建争议处理记录
        DisputeResolution disputeResolution = DisputeResolution.builder()
                .orderId(order.getId())
                .sourceType(DisputeResolutionConstant.CANCEL_APPLY)
                .sourceId(applicationId)
                .defaultingParty(defaultingParty)
                .decision(decision)
                .operatorId(BaseContext.getUserId())
                .note(note)
                .createdTime(LocalDateTime.now())
                .build();
        disputeResolutionMapper.insertDisputeResolution(disputeResolution);
        log.info("争议处理记录已创建，订单ID: {}，申请ID: {}，裁决结果: {}，违约方: {}", 
                order.getId(), applicationId, decision, defaultingParty);
        
        if (CancelApplicationStatusConstant.DECISION_AGREE.equals(decision) || 
            CancelApplicationStatusConstant.DECISION_PARTIAL.equals(decision)) {
            // 同意取消或部分结算，先执行结算
            settleOrder(order.getId(), application.getId());
            
            // 调用退款方法执行实际退款
            refund(order.getId());
            
            // 更新订单状态为已取消
            Order updateOrder = Order.builder()
                    .id(order.getId())
                    .status(OrderStatusConstant.CANCELLED)
                    .cancelType(getCancelTypeFromApplication(application.getCancelType()))
                    .cancelTime(LocalDateTime.now())
                    .build();
            orderMapper.updateOrder(updateOrder);
        }
        // 如果拒绝取消，订单继续
    }

    /**
     * @description 订单结算
     * 统一入口：根据订单状态和场景自动选择合适的结算方式
     * @author CyberCaelum
     * @date 上午11:07 2026/3/16
     * @param orderId 订单id
     * @param cancelApplicationId 取消申请id（正常完成时可传null）
     **/
    @Override
    @Transactional
    public void settleOrder(Long orderId, Long cancelApplicationId) {
        Order order = orderMapper.getOrderById(orderId);
        if (order == null) {
            throw new OrderNotFoundException("订单不存在");
        }

        SettlementCalculationResult result;
        
        if (cancelApplicationId == null) {
            // 正常订单完成结算
            result = calculateSettlement(order, 1, null);
        } else {
            // 查询取消申请信息
            CancelApplication application = cancelApplicationMapper.selectById(cancelApplicationId);
            if (application == null) {
                throw new OrderNotFoundException("取消申请不存在");
            }
            
            // 查询争议处理结果（使用sourceId精确查询对应申请的裁决结果）
            DisputeResolution disputeResolution = disputeResolutionMapper.selectBySourceIdAndType(
                    cancelApplicationId, DisputeResolutionConstant.CANCEL_APPLY);
            
            if (disputeResolution != null) {
                // 平台裁决后的结算
                Integer decision = disputeResolution.getDecision();
                Integer defaultingParty = disputeResolution.getDefaultingParty();
                
                if (DisputeResolutionConstant.AGREE.equals(decision)) {
                    // 全额退款
                    result = calculateSettlement(order, 3, defaultingParty);
                } else if (DisputeResolutionConstant.PARTIAL_SETTLEMENT.equals(decision)) {
                    // 部分结算
                    result = calculateSettlement(order, 4, defaultingParty);
                } else {
                    // 其他情况按协商一致处理
                    result = calculateSettlement(order, 2, null);
                }
            } else {
                // 协商一致取消结算
                result = calculateSettlement(order, 2, null);
            }
        }

        // 保存结算记录
        saveSettlementRecord(order, result, null);
        
        log.info("订单结算完成，订单ID: {}，类型: {}，雇员所得: {}，雇主支付: {}", 
                orderId, result.getCalculationType(), result.getEmployeeFinal(), result.getEmployerPayable());
    }

    /**
     * @description 自动开始服务（检查到达开始时间的订单）
     * @author CyberCaelum
     * @date 2026/3/16
     **/
    @Override
    @Transactional
    public void autoStartService() {
        // 查询已接单且到达开始时间的订单
        LocalDate today = LocalDate.now();
        List<Order> orders = orderMapper.selectByStartTimeAndStatus(today, OrderStatusConstant.CONFIRMED);
        
        for (Order order : orders) {
            try {
                // 业务校验：订单不能被取消
                if (OrderStatusConstant.CANCELLED.equals(order.getStatus())) {
                    log.warn("订单已被取消，跳过自动开始，orderId: {}", order.getId());
                    continue;
                }
                
                // 业务校验：订单支付状态必须为已支付
                if (!PayStatusConstant.PAID.equals(order.getPayStatus())) {
                    log.warn("订单未支付，跳过自动开始，orderId: {}", order.getId());
                    continue;
                }
                
                // 更新订单状态为进行中（服务中）
                Order updateOrder = Order.builder()
                        .id(order.getId())
                        .status(OrderStatusConstant.IN_PROGRESS)
                        .build();
                orderMapper.updateOrder(updateOrder);
                log.info("订单自动开始服务，orderId: {}", order.getId());
            } catch (Exception e) {
                log.error("订单自动开始服务失败，orderId: {}", order.getId(), e);
            }
        }
        
        log.info("执行自动开始服务定时任务完成，日期: {}，处理订单数: {}", today, orders.size());
    }

    /**
     * @description 自动确认每日服务（超时未确认的自动确认）
     * @author CyberCaelum
     * @date 2026/3/16
     **/
    @Override
    @Transactional
    public void autoConfirmDailyService() {
        // 查询需要自动确认的记录（家政人员已确认超过24小时，雇主未确认）
        LocalDateTime thresholdTime = LocalDateTime.now().minusHours(24);
        List<DailyConfirmation> needConfirmList = dailyConfirmationMapper.selectNeedAutoConfirm(thresholdTime);
        
        for (DailyConfirmation confirmation : needConfirmList) {
            // 更新为系统自动确认
            DailyConfirmation updateConfirmation = DailyConfirmation.builder()
                    .id(confirmation.getId())
                    .status(DailyConfirmationStatusConstant.AUTO_CONFIRMED)
                    .autoConfirmTime(LocalDateTime.now())
                    .build();
            dailyConfirmationMapper.update(updateConfirmation);
            
            log.info("每日服务自动确认，确认记录ID: {}，订单ID: {}，日期: {}", 
                    confirmation.getId(), confirmation.getOrderId(), confirmation.getServiceDate());
        }
        
        log.info("自动确认每日服务完成，共处理 {} 条记录", needConfirmList.size());
    }

    /**
     * @description 处理超时取消申请（转平台介入）
     * @author CyberCaelum
     * @date 2026/3/16
     **/
    @Override
    @Transactional
    public void processTimeoutCancelApplications() {
        // 查询超时的取消申请
        LocalDateTime now = LocalDateTime.now();
        List<CancelApplication> timeoutApps = cancelApplicationMapper.selectTimeoutApplications(now);
        
        for (CancelApplication application : timeoutApps) {
            // 更新状态为平台介入处理中
            application.setStatus(CancelApplicationStatusConstant.PLATFORM_PROCESSING);
            cancelApplicationMapper.update(application);
            
            // TODO 通知平台客服（简化处理，实际应发送通知）
            log.info("取消申请超时转平台介入，申请ID: {}，订单ID: {}", 
                    application.getId(), application.getOrderId());
        }
        
        log.info("处理超时取消申请完成，共处理 {} 条记录", timeoutApps.size());
    }
////////////////////////////////////////////////////////////////////
    /**
     * @description 订单超时处理
     * @author CyberCaelum
     * @date 上午10:03 2026/3/18
     * @param orderId 订单号
     **/
    @Override
    @Transactional
    public void orderTimeOut(Long orderId) {
        //查看订单状态为待支付
        Order order = orderMapper.getOrderById(orderId);
        // 订单不存在或已支付，直接返回
        if (order == null || !PayStatusConstant.UN_PAID.equals(order.getPayStatus())) {
            log.info("订单不存在或已支付，无需取消，订单ID: {}", orderId);
            return;
        }
        // 只有待付款状态的订单才允许超时取消
        if (!OrderStatusConstant.PENDING_PAYMENT.equals(order.getStatus())) {
            log.info("订单状态不是待付款，无需取消，订单ID: {}，当前状态: {}", orderId, order.getStatus());
            return;
        }
        //设置订单状态为取消
        order.setStatus(OrderStatusConstant.CANCELLED);
        //设置订单取消类型为平台取消
        order.setCancelType(CancelApplicationStatusConstant.TYPE_PLATFORM_FORCE);
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason("超时未支付自动取消");
        //更新订单数据库
        orderMapper.updateOrder(order);
        log.info("订单超时未支付已自动取消，订单ID: {}", orderId);
    }

    /**
     * @description 拒单退款成功回调处理（全额退款给雇主，家政人员无收入）
     * @author CyberCaelum
     * @date 2026/3/20
     * @param orderNo 订单号
     * @param refundNo 退款单号
     * @param refundFee 退款金额（分）
     **/
    @Override
    @Transactional
    public void rejectionRefundSuccess(String orderNo, String refundNo, Integer refundFee) {
        // 根据订单号查询订单
        Order order = orderMapper.getOrderByNumber(orderNo);
        if (order == null) {
            log.error("拒单退款回调处理失败：订单不存在，订单号: {}", orderNo);
            throw new OrderNotFoundException("订单不存在");
        }
        
        // 幂等性检查：如果订单已经退款，直接返回
        if (PayStatusConstant.REFUNDED.equals(order.getPayStatus())) {
            log.info("订单已退款，无需重复处理，订单号: {}", orderNo);
            return;
        }
        
        Long orderId = order.getId();
        
        // 使用统一的结算计算方法（全额退款，雇员违约）
        SettlementCalculationResult result = calculateSettlement(order, 3, DisputeResolutionConstant.EMPLOYEE_DEFAULTING);
        
        // 更新订单信息
        Order updateOrder = Order.builder()
                .id(orderId)
                .status(OrderStatusConstant.CANCELLED)
                .payStatus(PayStatusConstant.REFUNDED)
                .refundTime(LocalDateTime.now())
                .refundNumber(refundNo)
                .heldAmount(BigDecimal.ZERO) // 托管金额清0
                .build();
        orderMapper.updateOrder(updateOrder);
        
        // 保存结算记录
        saveSettlementRecord(order, result, refundNo);
        
        log.info("拒单退款成功处理完成，订单号: {}，退款金额: {}分，雇主全额退款，家政人员无收入", 
                orderNo, refundFee);
    }

    /**
     * @description 退款成功回调处理
     * 只更新订单状态，结算逻辑统一由 settleOrder 处理
     * @author CyberCaelum
     * @date 2026/3/20
     * @param orderNo 订单号
     * @param refundNo 退款单号
     * @param refundFee 退款金额（分）
     **/
    @Override
    @Transactional
    public void refundSuccess(String orderNo, String refundNo, Integer refundFee) {
        // 根据订单号查询订单
        Order order = orderMapper.getOrderByNumber(orderNo);
        if (order == null) {
            log.error("退款回调处理失败：订单不存在，订单号: {}", orderNo);
            throw new OrderNotFoundException("订单不存在");
        }
        
        // 幂等性检查：如果订单已经退款，直接返回
        if (PayStatusConstant.REFUNDED.equals(order.getPayStatus())) {
            log.info("订单已退款，无需重复处理，订单号: {}", orderNo);
            return;
        }
        
        Long orderId = order.getId();
        
        // 查询争议处理结果获取违约方信息（用于更新托管金额）
        DisputeResolution disputeResolution = disputeResolutionMapper.selectBySourceIdAndType(
                orderId, DisputeResolutionConstant.CANCEL_APPLY);
        
        // 更新订单状态为已退款（结算记录已由 settleOrder 创建）
        Order updateOrder = Order.builder()
                .id(orderId)
                .status(OrderStatusConstant.CANCELLED)  // 订单状态改为已取消
                .payStatus(PayStatusConstant.REFUNDED)  // 支付状态改为已退款
                .refundTime(LocalDateTime.now())        // 退款时间
                .cancelType(CancelApplicationStatusConstant.TYPE_PLATFORM_FORCE)  // 平台强制取消
                .refundNumber(refundNo)                 // 退款单号
                .build();
        orderMapper.updateOrder(updateOrder);
        
        log.info("退款成功处理完成，订单号: {}，退款金额: {}分，结算记录已由settleOrder创建", 
                orderNo, refundFee);
    }

    /**
     * @description 处理退款超时，主动查询退款状态
     * @author CyberCaelum
     * @date 2026/3/20
     * @param orderId 订单ID
     * @param refundNo 退款单号
     **/
    @Override
    @Transactional
    public void handleRefundTimeout(Long orderId, String refundNo) {
        log.info("处理退款超时，订单ID: {}，退款单号: {}", orderId, refundNo);
        try {
            // 先查数据库，判断订单是否已退款
            Order order = orderMapper.getOrderById(orderId);
            if (order == null) {
                log.warn("订单不存在，订单ID: {}", orderId);
                return;
            }
            
            // 如果订单已退款，说明回调已处理，直接返回
            if (PayStatusConstant.REFUNDED.equals(order.getPayStatus())) {
                log.info("订单已退款，回调已处理，无需查询微信，订单ID: {}", orderId);
                return;
            }
            
            // 数据库未更新，再去微信查询退款状态（兜底补偿）
            WxPayRefundQueryResult refundResult = wechatPayUtil.queryRefund(refundNo);
            
            // 获取退款记录列表
            List<WxPayRefundQueryResult.RefundRecord> refundRecords = refundResult.getRefundRecords();
            if (refundRecords == null || refundRecords.isEmpty()) {
                log.warn("未找到退款记录，订单ID: {}，退款单号: {}", orderId, refundNo);
                return;
            }
            
            // 查找对应的退款记录
            WxPayRefundQueryResult.RefundRecord targetRecord = null;
            for (WxPayRefundQueryResult.RefundRecord record : refundRecords) {
                if (refundNo.equals(record.getOutRefundNo())) {
                    targetRecord = record;
                    break;
                }
            }
            
            if (targetRecord == null) {
                log.warn("未找到指定退款单号的记录，订单ID: {}，退款单号: {}", orderId, refundNo);
                return;
            }
            
            // 获取退款状态
            String refundStatus = targetRecord.getRefundStatus();  // SUCCESS-退款成功，PROCESSING-退款处理中，CHANGE-退款异常，FAIL-退款失败
            
            if ("SUCCESS".equals(refundStatus)) {
                // 退款成功，调用退款成功处理
                String orderNo = refundResult.getOutTradeNo();
                Integer refundFee = targetRecord.getSettlementRefundFee();
                
                log.info("查询到退款成功，订单号: {}，退款单号: {}，金额: {}分", orderNo, refundNo, refundFee);

                // 判断是否是拒单退款：检查是否有争议裁决记录
                DisputeResolution disputeResolution = disputeResolutionMapper.selectDisputeResolutionByOrderId(
                        orderId, DisputeResolutionConstant.CANCEL_APPLY);

                if (disputeResolution == null) {
                    // 没有争议裁决记录，说明是拒单退款
                    rejectionRefundSuccess(orderNo, refundNo, refundFee);
                } else {
                    // 有争议裁决记录，按裁决结果处理
                    refundSuccess(orderNo, refundNo, refundFee);
                }
                
            } else if ("PROCESSING".equals(refundStatus)) {
                // 退款处理中，继续等待，可以再次发送延迟消息
                log.info("退款处理中，继续等待，订单ID: {}，退款单号: {}", orderId, refundNo);
                // 可以再次发送延迟消息，或者依赖微信的回调
                
            } else if ("CHANGE".equals(refundStatus) || "FAIL".equals(refundStatus)) {
                // 退款异常或失败，记录异常，人工介入
                log.error("退款异常或失败，订单ID: {}，退款单号: {}，状态: {}", orderId, refundNo, refundStatus);
                // TODO: 发送通知给管理员，人工处理
                
            } else {
                log.warn("未知的退款状态，订单ID: {}，退款单号: {}，状态: {}", orderId, refundNo, refundStatus);
            }
            
        } catch (Exception e) {
            log.error("查询退款状态失败，订单ID: {}，退款单号: {}", orderId, refundNo, e);
            // 不抛出异常，让消息消费成功，避免无限重试
        }
    }

    /**
     * @description 处理支付超时（回调保底），主动查询支付状态
     * @author CyberCaelum
     * @date 2026/3/20
     * @param orderId 订单ID
     * @param orderNumber 订单号
     **/
    @Override
    @Transactional
    public void handlePayTimeout(Long orderId, String orderNumber) {
        log.info("处理支付超时（回调保底），订单ID: {}，订单号: {}", orderId, orderNumber);
        
        try {
            // 先查数据库，判断订单是否已支付
            Order order = orderMapper.getOrderById(orderId);
            if (order == null) {
                log.warn("订单不存在，订单ID: {}", orderId);
                return;
            }
            
            //  如果订单已支付，说明回调已处理，直接返回
            if (PayStatusConstant.PAID.equals(order.getPayStatus())) {
                log.info("订单已支付，回调已处理，无需查询微信，订单ID: {}", orderId);
                return;
            }
            
            // 如果订单已取消，直接返回
            if (OrderStatusConstant.CANCELLED.equals(order.getStatus())) {
                log.info("订单已取消，无需查询微信，订单ID: {}", orderId);
                return;
            }
            
            // 数据库未更新，再去微信查询支付状态（兜底补偿）
            WxPayOrderQueryResult queryResult = wechatPayUtil.queryOrder(orderNumber);
            String tradeState = queryResult.getTradeState();
            
            if ("SUCCESS".equals(tradeState)) {
                // 支付成功，调用支付成功处理
                log.info("查询到支付成功，订单号: {}，微信订单号: {}", 
                        orderNumber, queryResult.getTransactionId());
                
                // 调用支付成功处理（幂等性检查在 paySuccess 方法中）
                // 从微信查询结果获取实际支付金额（分）
                Integer totalFee = queryResult.getTotalFee() != null ? queryResult.getTotalFee() : null;
                paySuccess(orderNumber, PayMethodConstant.WECHAT_PAY, totalFee);
                
            } else if ("NOTPAY".equals(tradeState)) {
                // 未支付，继续等待
                log.info("订单未支付，继续等待，订单ID: {}，订单号: {}", orderId, orderNumber);
                
            } else if ("CLOSED".equals(tradeState)) {
                // 订单已关闭
                log.info("订单已关闭，订单ID: {}，订单号: {}", orderId, orderNumber);
                
            } else if ("REVOKED".equals(tradeState)) {
                // 订单已撤销
                log.info("订单已撤销，订单ID: {}，订单号: {}", orderId, orderNumber);
                
            } else {
                log.warn("未知的支付状态，订单ID: {}，订单号: {}，状态: {}", orderId, orderNumber, tradeState);
            }
            
        } catch (Exception e) {
            log.error("查询支付状态失败，订单ID: {}，订单号: {}", orderId, orderNumber, e);
            // 不抛出异常，让消息消费成功，避免无限重试
        }
    }
}
