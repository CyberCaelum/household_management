package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.pojo.dto.*;
import org.cybercaelum.household_management.pojo.entity.PageResult;
import org.cybercaelum.household_management.pojo.vo.*;
import org.cybercaelum.household_management.service.OrderService;
import org.springframework.stereotype.Service;

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
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
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
