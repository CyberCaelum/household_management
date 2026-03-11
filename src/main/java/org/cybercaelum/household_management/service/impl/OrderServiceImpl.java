package org.cybercaelum.household_management.service.impl;

import com.fasterxml.jackson.databind.util.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.time.temporal.ChronoUnit;

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
        //返回数据
        return null;
    }

    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        return null;
    }

    @Override
    public void paySuccess(String outTradeNo) {

    }

    @Override
    public PageResult history(Integer page, Integer pageSize, Integer status) {
        return null;
    }

    @Override
    public void cancel(Long id) {

    }

    @Override
    public OrderVO details(Long id) {
        return null;
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
