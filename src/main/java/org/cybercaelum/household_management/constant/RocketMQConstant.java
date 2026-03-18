package org.cybercaelum.household_management.constant;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 消息队列常量
 * @date 2026/3/17
 */
public class RocketMQConstant {
    /**
     * 消息主题
     */
    public static final String ORDER_TIMEOUT_TOPIC = "ORDER_TIMEOUT_TOPIC";
    /**
     * 消息标签
     */
    public static final String ORDER_CANCEL_TAG = "ORDER_CANCEL";
    /**
     * 超时时间30分钟
     */
    public static final Long ORDER_TIMEOUT_DEFAULT = 1800000L;
}
