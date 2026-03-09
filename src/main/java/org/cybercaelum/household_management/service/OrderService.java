package org.cybercaelum.household_management.service;

import org.cybercaelum.household_management.pojo.dto.*;
import org.cybercaelum.household_management.pojo.entity.PageResult;
import org.cybercaelum.household_management.pojo.vo.*;

public interface OrderService {

    //用户下单
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    //订单支付
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);

    //支付成功，修改订单状态
    void paySuccess(String outTradeNo);

    //查看历史订单
    PageResult history(Integer page, Integer pageSize, Integer status);

    //取消订单
    void cancel(Long id);

    //查看订单详情
    OrderVO details(Long id);

    //再来一单
    void repetition(Long id);

    //查看订单详情和订单信息
    OrderDetailVO detail(Long id);
    //订单搜索
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    //各个状态订单数量统计
    OrderStatisticsVO statistics();

    //接单
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    //拒单
    void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    //商家取消订单
    void adminCancel(OrdersCancelDTO ordersCancelDTO);

    //派送订单
    void delivery(Long id);

    //完成订单
    void complete(Long id);

    //用户催单
    void reminder(Long id);
}
