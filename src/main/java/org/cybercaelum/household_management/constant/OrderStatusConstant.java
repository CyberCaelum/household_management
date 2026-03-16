package org.cybercaelum.household_management.constant;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 订单状态常量
 * @date 2026/3/12
 */
public class OrderStatusConstant {

    /**
     *待付款
     */
    public static final Integer PENDING_PAYMENT = 0;
    /**
     *已取消
     */
    public static final Integer CANCELLED = 1;
    /**
     *待被雇者确认
     */
    public static final Integer TO_BE_CONFIRMED = 2;
    /**
     *已接单
     */
    public static final Integer CONFIRMED = 3;
    /**
     *进行中
     */
    public static final Integer IN_PROGRESS = 4;
    /**
     *已完成
     */
    public static final Integer COMPLETED = 5;
    /**
     *退款
     */
    public static final Integer REFUND = 6;
}
