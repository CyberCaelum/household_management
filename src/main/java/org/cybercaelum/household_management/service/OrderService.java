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

    //取消订单（用户发起取消申请）
    void cancel(Long id,String reason);

    //查看订单详情
    OrderVO details(Long id);

//    //再来一单
//    void repetition(Long id);

    //查看订单详情和订单信息
    OrderDetailVO detail(Long id);
//    //订单搜索
//    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    //各个状态订单数量统计
    OrderStatisticsVO statistics();

    //接单（被雇者确认接单）
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    //拒单（被雇者拒绝接单）
    void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    //商家/管理员取消订单
    void adminCancel(OrdersCancelDTO ordersCancelDTO);

    //派送订单（标记订单为进行中/服务中）
    void delivery(Long id);

    //完成订单
    void complete(Long id);

    //用户催单
    void reminder(Long id);

    // ==================== 每日确认相关 ====================

    /**
     * 家政人员每日服务完成确认
     * @param orderId 订单ID
     * @param serviceDate 服务日期
     */
    void workerDailyConfirm(Long orderId, java.time.LocalDate serviceDate);

    /**
     * 雇主确认每日服务
     * @param confirmationId 确认记录ID
     */
    void employerDailyConfirm(Long confirmationId);

    /**
     * 雇主对每日服务提出争议
     * @param confirmationId 确认记录ID
     * @param reason 争议原因
     */
    void employerDisputeDaily(Long confirmationId, String reason);

    // ==================== 取消申请相关 ====================

    /**
     * 发起取消申请
     * @param orderId 订单ID
     * @param cancelType 取消类型：1-协商一致，2-雇主强制，3-家政人员强制
     * @param reason 申请理由
     */
    void applyCancel(Long orderId, Integer cancelType, String reason);

    /**
     * 响应取消申请（同意或拒绝）
     * @param applicationId 申请ID
     * @param agree 是否同意
     */
    void respondCancelApplication(Long applicationId, Boolean agree);

    /**
     * 平台裁决取消申请
     * @param applicationId 申请ID
     * @param decision 裁决结果：1-同意取消，2-拒绝取消，3-部分结算
     * @param note 平台备注
     */
    void platformDecideCancelApplication(Long applicationId, Integer decision, String note);

    // ==================== 结算相关 ====================

    /**
     * 执行订单结算
     * @param orderId 订单ID
     * @param cancelApplicationId 关联的取消申请ID（可选）
     */
    void settleOrder(Long orderId, Long cancelApplicationId);

    // ==================== 定时任务相关 ====================

    /**
     * 自动开始服务（检查到达开始时间的订单）
     */
    void autoStartService();

    /**
     * 自动确认每日服务（超时未确认的自动确认）
     */
    void autoConfirmDailyService();

    /**
     * 处理超时取消申请（转平台介入）
     */
    void processTimeoutCancelApplications();
}
