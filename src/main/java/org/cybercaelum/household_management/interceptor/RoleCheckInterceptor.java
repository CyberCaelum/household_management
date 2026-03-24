package org.cybercaelum.household_management.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.annotation.RequireRole;
import org.cybercaelum.household_management.constant.JwtClaimsConstant;
import org.cybercaelum.household_management.context.BaseContext;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 角色权限校验拦截器
 * @date: 2026/3/24
 * 
 * 功能：
 * 1. 检查 Controller 方法或类上的 @RequireRole 注解
 * 2. 校验当前用户角色是否在允许列表中
 * 3. 无注解的方法直接放行
 */
@Component
@Slf4j
public class RoleCheckInterceptor implements HandlerInterceptor {

    /**
     * 前置处理：校验角色权限
     */
    @Override
    public boolean preHandle(HttpServletRequest request, 
                             HttpServletResponse response, 
                             Object handler) throws Exception {
        
        // 1. 判断是否是 Controller 方法（静态资源直接放行）
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 2. 获取方法上的注解
        RequireRole methodAnnotation = handlerMethod.getMethodAnnotation(RequireRole.class);
        
        // 3. 获取类上的注解（方法上没有时，使用类上的）
        RequireRole classAnnotation = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        
        // 4. 合并判断：优先使用方法上的注解
        RequireRole targetAnnotation = methodAnnotation != null ? methodAnnotation : classAnnotation;
        
        // 5. 没有注解，直接放行
        if (targetAnnotation == null) {
            return true;
        }

        // 6. 从 ThreadLocal 获取当前用户角色
        Integer currentRole = BaseContext.getRole();
        
        if (currentRole == null) {
            log.warn("权限校验失败：无法获取用户角色，URI: {}", request.getRequestURI());
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或登录已过期\"}");
            return false;
        }

        // 7. 检查当前角色是否在允许列表中
        int[] allowedRoles = targetAnnotation.value();
        boolean hasPermission = Arrays.stream(allowedRoles)
                .anyMatch(role -> role == currentRole);

        if (!hasPermission) {
            log.warn("权限校验失败：用户角色 {} 无权访问 {}，需要角色: {}", 
                    currentRole, request.getRequestURI(), Arrays.toString(allowedRoles));
            response.setStatus(403);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":403,\"msg\":\"无权限访问该资源\"}");
            return false;
        }

        log.debug("权限校验通过：用户角色 {} 访问 {}", currentRole, request.getRequestURI());
        return true;
    }
}
