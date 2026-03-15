package org.cybercaelum.household_management.constant;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 每日确认状态常量
 * @date 2026/3/15
 */
public class DailyConfirmationStatusConstant {

    /**
     * 确认状态
     */
    public static final Integer PENDING = 0;        // 待确认
    public static final Integer EMPLOYER_CONFIRMED = 1;  // 雇主已确认
    public static final Integer EMPLOYER_REJECTED = 2;   // 雇主拒绝/争议
    public static final Integer AUTO_CONFIRMED = 3;      // 系统自动确认
}
