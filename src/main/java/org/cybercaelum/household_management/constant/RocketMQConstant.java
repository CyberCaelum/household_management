package org.cybercaelum.household_management.constant;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 消息队列常量
 * @date 2026/3/17
 */
public class RocketMQConstant {
    /**
     * 消息主题-订单超时
     */
    public static final String ORDER_TIMEOUT_TOPIC = "ORDER_TIMEOUT_TOPIC";
    /**
     * 消息标签-订单取消
     */
    public static final String ORDER_CANCEL_TAG = "ORDER_CANCEL";
    /**
     * 消息主题-退款超时
     */
    public static final String REFUND_TIMEOUT_TOPIC = "REFUND_TIMEOUT_TOPIC";
    /**
     * 消息标签-退款超时处理
     */
    public static final String REFUND_TIMEOUT_TAG = "REFUND_TIMEOUT";
    /**
     * 消息主题-支付超时（回调保底）
     */
    public static final String PAY_TIMEOUT_TOPIC = "PAY_TIMEOUT_TOPIC";
    /**
     * 消息标签-支付超时处理（主动查询支付状态）
     */
    public static final String PAY_TIMEOUT_TAG = "PAY_TIMEOUT";
    /**
     * 超时时间30分钟
     */
    public static final Long ORDER_TIMEOUT_DEFAULT = 1800000L;
    /**
     * 支付回调超时时间5分钟
     */
    public static final Long PAY_TIMEOUT_DEFAULT = 300000L;
}
