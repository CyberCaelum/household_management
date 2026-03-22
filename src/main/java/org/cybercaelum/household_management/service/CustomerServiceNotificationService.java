package org.cybercaelum.household_management.service;

/**
 * 客服通知服务
 * 用于向订单群组发送各类系统通知
 * @author CyberCaelum
 * @version 1.0
 * @date 2026/3/22
 */
public interface CustomerServiceNotificationService {

    /**
     * 通知每日服务争议
     * @param orderId 订单ID
     * @param confirmationId 确认记录ID
     * @param serviceDate 服务日期
     * @param reason 争议原因
     */
    void notifyDailyServiceDispute(Long orderId, Long confirmationId, String serviceDate, String reason);

    /**
     * 通知取消申请超时（转平台介入）
     * @param orderId 订单ID
     * @param applicationId 申请ID
     * @param cancelType 取消类型
     */
    void notifyCancelApplicationTimeout(Long orderId, Long applicationId, Integer cancelType);

    /**
     * 通知退款异常需要人工处理
     * @param orderId 订单ID
     * @param refundNo 退款单号
     * @param errorMsg 错误信息
     */
    void notifyRefundException(Long orderId, String refundNo, String errorMsg);

    /**
     * 通知订单异常（通用）
     * @param orderId 订单ID
     * @param exceptionType 异常类型
     * @param message 异常信息
     */
    void notifyOrderException(Long orderId, String exceptionType, String message);

    /**
     * 通知订单支付成功
     * @param orderId 订单ID
     * @param amount 支付金额
     */
    void notifyOrderPaid(Long orderId, String amount);

    /**
     * 通知订单退款成功
     * @param orderId 订单ID
     * @param refundAmount 退款金额
     * @param reason 退款原因
     */
    void notifyOrderRefunded(Long orderId, String refundAmount, String reason);
}
