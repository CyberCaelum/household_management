package org.cybercaelum.household_management.constant;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 每日确认状态常量
 * @date 2026/3/15
 */
public class DailyConfirmationStatusConstant {
    /**
     * 待确认
     */
    public static final Integer PENDING = 0;
    /**
     * 雇主已确认
     */
    public static final Integer EMPLOYER_CONFIRMED = 1;
    /**
     * 雇主拒绝/争议
     */
    public static final Integer EMPLOYER_REJECTED = 2;
    /**
     * 系统自动确认
     */
    public static final Integer AUTO_CONFIRMED = 3;
    /**
     * 争议成立（平台同意争议）
     */
    public static final Integer DISPUTE_UPHELD = 4;
    /**
     * 争议驳回（平台拒绝争议，恢复确认）
     */
    public static final Integer DISPUTE_REJECTED = 5;
}
