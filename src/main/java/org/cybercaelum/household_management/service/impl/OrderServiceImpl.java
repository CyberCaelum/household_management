package org.cybercaelum.household_management.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.constant.*;
import org.cybercaelum.household_management.context.BaseContext;
import org.cybercaelum.household_management.exception.*;
import org.cybercaelum.household_management.mapper.*;
import org.cybercaelum.household_management.pojo.dto.*;
import org.cybercaelum.household_management.pojo.entity.*;
import org.cybercaelum.household_management.pojo.vo.*;
import org.cybercaelum.household_management.service.OrderService;
import org.cybercaelum.household_management.service.RecruitmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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
                .orderNumber(String.valueOf(System.currentTimeMillis()))//订单号
                .cancel_type(CancelApplicationStatusConstant.TYPE_NOT_CANCELLED)//设置状态为未取消
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
        // 生成每日确认记录
        generateDailyConfirmations(order);
        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();
        BeanUtils.copyProperties(order,orderSubmitVO);
        //设置订单金额
        orderSubmitVO.setOrderAmount(total);
        // 设置招募信息为隐藏
        recruitmentService.updateRecruitmentStatus(RecruitmentStatusConstant.HIDDEN,order.getId());
        //返回数据
        return orderSubmitVO;
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

    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        return null;
    }

    /**
     * @description 支付成功修改订单状态
     * @author CyberCaelum
     * @date 上午10:32 2026/3/13
     * @param orderNumber 订单号
     **/
    @Override
    public void paySuccess(String orderNumber,Integer payMethod) {
        //根据订单号查询订单
        Order order = orderMapper.getOrderByNumber(orderNumber);
        //修改订单状态和信息
        Order payedOrder = Order.builder()
                .id(order.getId())//主键
                .payStatus(PayStatusConstant.PAID)//已支付
                .paymentTime(LocalDateTime.now())//支付时间
                .status(OrderStatusConstant.TO_BE_CONFIRMED)//从未支付变为待确认
                .payMethod(payMethod)//支付方式
                .build();
        orderMapper.updateOrder(payedOrder);
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
     * @description 用户取消订单（发起取消申请）
     * @author CyberCaelum
     * @date 2026/3/15
     * @param id 订单id
     **/
    @Override
    @Transactional
    public void cancel(Long id,String reason) {
        //获取订单信息
        Order order = orderMapper.getOrderById(id);
        if (order == null) {
            throw new OrderNotFoundException("订单不存在");
        }
        
        // 验证权限（只能是雇主或家政人员）
        Long userId = BaseContext.getUserId();
//        Integer role = BaseContext.getRole();
        if (!userId.equals(order.getEmployerId()) && !userId.equals(order.getEmployeeId())) {
            throw new PermissionDeniedException("无权操作此订单");
        }
        
        // 发起协商取消申请
        Integer cancelType = CancelApplicationStatusConstant.TYPE_NEGOTIATED;
        applyCancel(id, cancelType, reason);
    }

    /**
     * @description 查看订单详细信息
     * @author CyberCaelum
     * @date 上午11:00 2026/3/13
     * @param id 订单主键
     * @return org.cybercaelum.household_management.pojo.vo.OrderVO
     **/
    @Override
    public OrderVO details(Long id) {
        Order order = orderMapper.getOrderById(id);
        if (order == null) {
            throw new OrderNotFoundException("订单不存在");
        }
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order,orderVO);
        return orderVO;
    }

//    @Override
//    public void repetition(Long id) {
//        // 实现再来一单逻辑
//        Order order = orderMapper.getOrderById(id);
//        if (order == null) {
//            throw new OrderNotFoundException("订单不存在");
//        }
//
//        // 创建新订单，复制原订单信息
//        Order newOrder = Order.builder()
//                .price(order.getPrice())
//                .orderTime(LocalDateTime.now())
//                .recruitmentId(order.getRecruitmentId())
//                .status(OrderStatusConstant.TO_BE_CONFIRMED)
//                .startTime(order.getStartTime())
//                .endTime(order.getEndTime())
//                .employerId(order.getEmployerId())
//                .employeeId(order.getEmployeeId())
//                .provinceCode(order.getProvinceCode())
//                .provinceName(order.getProvinceName())
//                .cityCode(order.getCityCode())
//                .cityName(order.getCityName())
//                .districtCode(order.getDistrictCode())
//                .districtName(order.getDistrictName())
//                .detail(order.getDetail())
//                .total(order.getTotal())
//                .days(order.getDays())
//                .orderNumber(String.valueOf(System.currentTimeMillis()))
//                .build();
//
//        orderMapper.insertOrder(newOrder);
//        generateDailyConfirmations(newOrder);
//    }

    @Override
    public OrderDetailVO detail(Long id) {
        Order order = orderMapper.getOrderById(id);
        if (order == null) {
            throw new OrderNotFoundException("订单不存在");
        }
        
        OrderDetailVO detailVO = new OrderDetailVO();
        BeanUtils.copyProperties(order, detailVO);
        return detailVO;
    }

//    @Override
//    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
//        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
//        // 这里需要根据实际条件查询
//        Page<Order> orders = orderMapper.history(null, ordersPageQueryDTO.getStatus());
//        List<OrderVO> list = new ArrayList<>();
//        if (orders != null && !orders.isEmpty()) {
//            for (Order order : orders) {
//                OrderVO orderVO = new OrderVO();
//                BeanUtils.copyProperties(order, orderVO);
//                list.add(orderVO);
//            }
//        }
//        return new PageResult(orders.getTotal(), list);
//    }

//    @Override
//    public OrderStatisticsVO statistics() {
//        // 统计各状态订单数量
//        Long userId = BaseContext.getUserId();
//
//        Integer toBeConfirmed = orderMapper.countByStatusAndUserId(OrderStatusConstant.TO_BE_CONFIRMED, userId);
//        Integer confirmed = orderMapper.countByStatusAndUserId(OrderStatusConstant.CONFIRMED, userId);
//        Integer inProgress = orderMapper.countByStatusAndUserId(OrderStatusConstant.IN_PROGRESS, userId);
//        Integer completed = orderMapper.countByStatusAndUserId(OrderStatusConstant.COMPLETED, userId);
//        Integer cancelled = orderMapper.countByStatusAndUserId(OrderStatusConstant.CANCELLED, userId);
//
//        return OrderStatisticsVO.builder()
//                .toBeConfirmed(toBeConfirmed != null ? toBeConfirmed : 0)
//                .confirmed(confirmed != null ? confirmed : 0)
//                .inProgress(inProgress != null ? inProgress : 0)
//                .completed(completed != null ? completed : 0)
//                .cancelled(cancelled != null ? cancelled : 0)
//                .build();
//    }

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
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        Long orderId = ordersRejectionDTO.getId();
        Order order = orderMapper.getOrderById(orderId);
        if (order == null) {
            throw new OrderNotFoundException("订单不存在");
        }
        
        // 验证订单状态
        if (!OrderStatusConstant.TO_BE_CONFIRMED.equals(order.getStatus())) {
            throw new OrderStatusErrorException("订单状态错误，无法拒单");
        }
        
        // 更新订单状态为已取消，记录拒单原因
        Order updateOrder = Order.builder()
                .id(orderId)
                .status(OrderStatusConstant.CANCELLED)
                .rejectionReason(ordersRejectionDTO.getRejectionReason())
                .rejectionTime(LocalDateTime.now())
                .refundTime(LocalDateTime.now())
                .build();
        //TODO 退单，退款
        orderMapper.updateOrder(updateOrder);
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
        
        // 更新订单状态为已取消
        Order updateOrder = Order.builder()
                .id(orderId)
                .status(OrderStatusConstant.CANCELLED)
                .cancelReason(ordersCancelDTO.getCancelReason())
                .cancelTime(LocalDateTime.now())
                .cancel_type(4) // 平台取消
                .build();
        orderMapper.updateOrder(updateOrder);
        //TODO 更新结算表
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
        
        // 更新订单状态为已完成
        Order updateOrder = Order.builder()
                .id(id)
                .status(OrderStatusConstant.COMPLETED)
                .build();
        orderMapper.updateOrder(updateOrder);
        
        // 执行结算
        settleOrder(id, null);
    }

//    @Override
//    public void reminder(Long id) {
//        // 催单逻辑，可发送通知
//        log.info("用户催单，订单ID: {}", id);
//    }

    // ==================== 每日确认相关 ====================

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
        dailyConfirmationMapper.update(updateConfirmation);
    }

    // ==================== 取消申请相关 ====================

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
        if (!OrderStatusConstant.IN_PROGRESS.equals(order.getStatus()) || //进行中
            !OrderStatusConstant.PENDING_PAYMENT.equals(order.getStatus()) || //待付款
            !OrderStatusConstant.TO_BE_CONFIRMED.equals(order.getStatus()) || //待被确认
            !OrderStatusConstant.CONFIRMED.equals(order.getStatus()) ) { //已接单
            throw new OrderStatusErrorException("当前订单状态无法取消");
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
                    .cancel_type(getCancelTypeFromApplication(application.getCancelType()))
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
     * @param note 平台备注
     **/
    @Override
    @Transactional
    public void platformDecideCancelApplication(Long applicationId, Integer decision, String note) {
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
        
        if (CancelApplicationStatusConstant.DECISION_AGREE.equals(decision) || 
            CancelApplicationStatusConstant.DECISION_PARTIAL.equals(decision)) {
            // 同意取消或部分结算
            settleOrder(order.getId(), application.getId());
            
            // 更新订单状态为已取消
            Order updateOrder = Order.builder()
                    .id(order.getId())
                    .status(OrderStatusConstant.CANCELLED)
                    .cancel_type(getCancelTypeFromApplication(application.getCancelType()))
                    .cancelTime(LocalDateTime.now())
                    .build();
            orderMapper.updateOrder(updateOrder);
        }
        // 如果拒绝取消，订单继续
    }

    // ==================== 结算相关 ====================

    /**
     * @description 订单结算
     * @author CyberCaelum
     * @date 上午11:07 2026/3/16
     * @param orderId 订单id
     * @param cancelApplicationId 取消申请id
     **/
    @Override
    @Transactional
    public void settleOrder(Long orderId, Long cancelApplicationId) {
        Order order = orderMapper.getOrderById(orderId);
        if (order == null) {
            throw new OrderNotFoundException("订单不存在");
        }
        
        // 统计已确认天数
        int totalDays = dailyConfirmationMapper.countConfirmedDays(orderId);
        
        // 计算金额，金额乘实际天数
        BigDecimal totalAmount = order.getPrice().multiply(new BigDecimal(totalDays));
        BigDecimal penaltyDeduction = BigDecimal.ZERO;

        BigDecimal finalAmount = totalAmount;
        // 如果有取消申请且为强制取消，计算违约金（简化处理，实际应按规则计算）
        if (cancelApplicationId != null) {
            CancelApplication application = cancelApplicationMapper.selectById(cancelApplicationId);
            if (application != null && 
                (CancelApplicationStatusConstant.TYPE_EMPLOYER_FORCE.equals(application.getCancelType()) ||
                 CancelApplicationStatusConstant.TYPE_WORKER_FORCE.equals(application.getCancelType()))) {
                //违约金为全部金额的10%
                penaltyDeduction = totalAmount.multiply(new BigDecimal("0.1"));
                //雇主强制取消，雇主多付钱
                if (CancelApplicationStatusConstant.TYPE_EMPLOYER_FORCE.equals(application.getCancelType())){
                    finalAmount = finalAmount.add(penaltyDeduction);
                } else {//家政人员强制取消，家政人员多付钱
                    finalAmount = finalAmount.subtract(penaltyDeduction);
                }
            }
        }

        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        // 创建结算记录
        Settlement settlement = Settlement.builder()
                .orderId(orderId)
                .totalDays(totalDays)
                .dailyRate(order.getPrice())
                .totalAmount(totalAmount)
                .penaltyDeduction(penaltyDeduction)
                .finalAmount(finalAmount)
                .status(SettlementStatusConstant.PENDING)
                .createTime(LocalDateTime.now())
                .build();
        
        settlementMapper.insert(settlement);
        
        // 调用支付系统完成转账（简化处理）
        // TODO: 调用支付系统
        
        // 更新结算状态为已结算
        settlement.setStatus(SettlementStatusConstant.SETTLED);
        settlement.setSettlementTime(LocalDateTime.now());
        settlementMapper.update(settlement);
        //TODO 更新订单信息
        log.info("订单结算完成，订单ID: {}，结算金额: {}", orderId, finalAmount);
    }

    // ==================== 定时任务相关 ====================

    /**
     * 自动开始服务（检查到达开始时间的订单）
     */
    @Override
    @Transactional
    public void autoStartService() {
        // 查询已接单且到达开始时间的订单
        LocalDate today = LocalDate.now();
        List<Order> orders = orderMapper.selectByStartTimeAndStatus(today, OrderStatusConstant.CONFIRMED);
        
        for (Order order : orders) {
            try {
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
     * 自动确认每日服务（超时未确认的自动确认）
     */
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
     * 处理超时取消申请（转平台介入）
     */
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
            
            // 通知平台客服（简化处理，实际应发送通知）
            log.info("取消申请超时转平台介入，申请ID: {}，订单ID: {}", 
                    application.getId(), application.getOrderId());
        }
        
        log.info("处理超时取消申请完成，共处理 {} 条记录", timeoutApps.size());
    }
}
