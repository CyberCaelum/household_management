package org.cybercaelum.household_management.constant;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 状态常量
 * @date 2025/10/23 下午7:15
 */
public class StatusConstant {

    /**
     * 通用状态 1启用，0禁用
     */
    public static final Integer ENABLE = 1;     // 启用
    public static final Integer DISABLE = 0;    // 禁用

    /**
     * 账号状态
     */
    public static final Integer ACCOUNT_CANCELLED = 0;  // 已注销
    public static final Integer ACCOUNT_ACTIVE = 1;     // 已启用
}
