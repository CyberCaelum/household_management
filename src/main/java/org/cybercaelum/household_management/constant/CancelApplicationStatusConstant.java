package org.cybercaelum.household_management.constant;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 取消申请状态常量
 * @date 2026/3/15
 */
public class CancelApplicationStatusConstant {

    /**
     * 申请状态
     */
    public static final Integer PENDING_CONFIRM = 1;     // 待对方确认
    public static final Integer CONFIRMED_AGREE = 2;     // 对方已同意
    public static final Integer CONFIRMED_REJECT = 3;    // 对方已拒绝
    public static final Integer PLATFORM_PROCESSING = 4; // 平台介入处理中
    public static final Integer PLATFORM_DECIDED = 5;    // 平台已裁决

    /**
     * 取消类型
     */
    public static final Integer TYPE_NEGOTIATED = 1;     // 协商一致取消
    public static final Integer TYPE_EMPLOYER_FORCE = 2; // 雇主强制取消
    public static final Integer TYPE_WORKER_FORCE = 3;   // 家政人员强制取消

    /**
     * 申请人角色
     */
    public static final Integer ROLE_EMPLOYER = 1;       // 雇主
    public static final Integer ROLE_WORKER = 2;         // 家政人员

    /**
     * 平台裁决结果
     */
    public static final Integer DECISION_AGREE = 1;      // 同意取消
    public static final Integer DECISION_REJECT = 2;     // 拒绝取消
    public static final Integer DECISION_PARTIAL = 3;    // 部分结算
}
