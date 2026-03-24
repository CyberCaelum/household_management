package org.cybercaelum.household_management.annotation;

import org.cybercaelum.household_management.constant.RoleConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 角色权限校验注解
 * @date: 2026/3/24
 * 
 * 使用示例：
 * @RequireRole(RoleConstant.ADMIN)                    // 仅管理员
 * @RequireRole({RoleConstant.ADMIN, RoleConstant.CUSTOMER_SERVICE})  // 管理员或客服
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    /**
     * 允许访问的角色列表
     * 默认：仅管理员
     */
    int[] value() default {RoleConstant.ADMIN};
}
