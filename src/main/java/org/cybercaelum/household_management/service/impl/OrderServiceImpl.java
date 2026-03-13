package org.cybercaelum.household_management.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.constant.OrderStatusConstant;
import org.cybercaelum.household_management.constant.PayStatusConstant;
import org.cybercaelum.household_management.context.BaseContext;
import org.cybercaelum.household_management.exception.OrderPriceException;
import org.cybercaelum.household_management.exception.RecruitmentNotFoundException;
import org.cybercaelum.household_management.mapper.OrderMapper;
import org.cybercaelum.household_management.mapper.RecruitmentMapper;
import org.cybercaelum.household_management.pojo.dto.*;
import org.cybercaelum.household_management.pojo.entity.Order;
import org.cybercaelum.household_management.pojo.entity.PageResult;
import org.cybercaelum.household_management.pojo.entity.Recruitment;
import org.cybercaelum.household_management.pojo.vo.*;
import org.cybercaelum.household_management.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    /**
     * @description 提交订单
     * @author CyberCaelum
     * @date 上午9:14 2026/3/12
     * @param ordersSubmitDTO 订单信息
     * @return org.cybercaelum.household_management.pojo.vo.OrderSubmitVO
     **/
    @Override
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
                .build();
        //复制地址
        BeanUtils.copyProperties(recruitment,order);
        //设置订单状态，待接单
        order.setStatus(3);
        //计算总价
        order.setTotal(ordersSubmitDTO.getPrice().multiply(new BigDecimal(ordersSubmitDTO.getDays())));
        //存入数据库
        orderMapper.insertOrder(order);
        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();
        BeanUtils.copyProperties(order,orderSubmitVO);
        //返回数据
        return orderSubmitVO;
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
    public void paySuccess(String orderNumber) {
        //根据订单号查询订单
        Order order = orderMapper.getOrderByNumber(orderNumber);
        //修改订单状态和信息
        Order payedOrder = Order.builder()
                .id(order.getId())//主键
                .payStatus(PayStatusConstant.PAID)//已支付
                .paymentTime(LocalDateTime.now())//支付时间
                .status(OrderStatusConstant.TO_BE_CONFIRMED)//从未支付变为待确认
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

    @Override
    public void cancel(Long id) {

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
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order,orderVO);
        return orderVO;
    }

    @Override
    public void repetition(Long id) {

    }

    @Override
    public OrderDetailVO detail(Long id) {
        return null;
    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        return null;
    }

    @Override
    public OrderStatisticsVO statistics() {
        return null;
    }

    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {

    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception {

    }

    @Override
    public void adminCancel(OrdersCancelDTO ordersCancelDTO) {

    }

    @Override
    public void delivery(Long id) {

    }

    @Override
    public void complete(Long id) {

    }

    @Override
    public void reminder(Long id) {

    }
}
