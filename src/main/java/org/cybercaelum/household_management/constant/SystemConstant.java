package org.cybercaelum.household_management.constant;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 系统常量汇总
 * @date 2026/3/12
 */
public class SystemConstant {

    // ==================== 订单状态 ====================
    /**
     * 订单状态 0待付款 1已取消 2待被雇者确认 3已接单 4进行中 5已完成 6退款
     */
    public static final Integer ORDER_PENDING_PAYMENT = 0;      // 待付款
    public static final Integer ORDER_CANCELLED = 1;            // 已取消
    public static final Integer ORDER_TO_BE_CONFIRMED = 2;      // 待被雇者确认
    public static final Integer ORDER_CONFIRMED = 3;            // 已接单
    public static final Integer ORDER_IN_PROGRESS = 4;          // 进行中
    public static final Integer ORDER_COMPLETED = 5;            // 已完成
    public static final Integer ORDER_REFUND = 6;               // 退款

    // ==================== 支付状态 ====================
    /**
     * 支付状态 0未支付 1已支付 2已退款
     */
    public static final Integer PAY_UN_PAID = 0;                // 未支付
    public static final Integer PAY_PAID = 1;                   // 已支付
    public static final Integer PAY_REFUNDED = 2;               // 已退款

    // ==================== 支付方式 ====================
    /**
     * 支付方式 1微信 2支付宝
     */
    public static final Integer PAY_METHOD_WECHAT = 1;          // 微信支付
    public static final Integer PAY_METHOD_ALIPAY = 2;          // 支付宝

    // ==================== 招募状态 ====================
    /**
     * 招募状态 0删除 1发布 2隐藏 3结束
     */
    public static final Integer RECRUITMENT_DELETED = 0;        // 已删除
    public static final Integer RECRUITMENT_PUBLISHED = 1;      // 已发布
    public static final Integer RECRUITMENT_HIDDEN = 2;         // 已隐藏
    public static final Integer RECRUITMENT_ENDED = 3;          // 已结束

    // ==================== 会话状态 ====================
    /**
     * 会话状态 0结束 1活动
     */
    public static final Integer SESSION_ENDED = 0;              // 已结束
    public static final Integer SESSION_ACTIVE = 1;             // 活动中

    // ==================== 评论状态 ====================
    /**
     * 评论状态 0删除 1可见
     */
    public static final Integer COMMENT_DELETED = 0;            // 已删除
    public static final Integer COMMENT_VISIBLE = 1;            // 可见

    // ==================== 简历可见性 ====================
    /**
     * 简历可见性 0不可见 1可见
     */
    public static final Integer RESUME_INVISIBLE = 0;           // 不可见
    public static final Integer RESUME_VISIBLE = 1;             // 可见

    // ==================== 账号状态 ====================
    /**
     * 账号状态 0注销 1启用
     */
    public static final Integer ACCOUNT_CANCELLED = 0;          // 已注销
    public static final Integer ACCOUNT_ACTIVE = 1;             // 已启用

    // ==================== 账号角色 ====================
    /**
     * 账号角色 0管理员 1用户
     */
    public static final Integer ROLE_ADMIN = 0;                 // 管理员
    public static final Integer ROLE_USER = 1;                  // 普通用户

    // ==================== 通用状态 ====================
    /**
     * 通用状态 0禁用 1启用
     */
    public static final Integer STATUS_DISABLE = 0;             // 禁用
    public static final Integer STATUS_ENABLE = 1;              // 启用
}
