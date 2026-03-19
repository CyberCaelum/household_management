package org.cybercaelum.household_management.constant;

import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 订单处理结果常量
 * @date 2026/3/19 上午9:03
 */
public class DisputeResolutionConstant {
    /**
     * 取消申请
     */
    public static final Integer CANCEL_APPLY = 1;
    /**
     * 每日确认
     */
    public static final Integer DAILY_CONFIRMATION = 2;
    /**
     * 其他
     */
    public static final Integer OTHERS = 3;

    /**
     * 雇主违约
     */
    public static final Integer EMPLOYER_DEFAULTING = 1;
    /**
     * 雇员违约
     */
    public static final Integer EMPLOYEE_DEFAULTING = 2;

    /**
     * 同意
     */
    public static final Integer AGREE = 1;
    /**
     * 拒绝
     */
    public static final Integer REJECT = 2;
    /**
     * 部分结算
     */
    public static final Integer PARTIAL_SETTLEMENT = 3;
}
