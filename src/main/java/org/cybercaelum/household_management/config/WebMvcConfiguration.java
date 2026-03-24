package org.cybercaelum.household_management.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.interceptor.JwtTokenInterceptor;
import org.cybercaelum.household_management.interceptor.RoleCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 配置类，注册web层相关组件
 * @date 2025/10/30 下午8:21
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    
    private final JwtTokenInterceptor jwtTokenInterceptor;
    private final RoleCheckInterceptor roleCheckInterceptor;
    
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        
        // 1. JWT 认证拦截器 - 最先执行，解析 token 并将用户信息放入 BaseContext
        registry.addInterceptor(jwtTokenInterceptor)
                .addPathPatterns("/user/**")
                .addPathPatterns("/recruitment/**")
                .addPathPatterns("/resume/add")
                .addPathPatterns("/resume/update")
                .addPathPatterns("/resume/visibility/**")
                .addPathPatterns("/comment/add")
                .addPathPatterns("/comment/update")
                .addPathPatterns("/comment/delete/**")
                .addPathPatterns("/comment/my")
                .addPathPatterns("/admin/**")  // 管理端也需要 JWT 认证
                .addPathPatterns("/kefu/**")   // 客服端也需要 JWT 认证
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        // 支付回调接口（微信调用，不需要JWT）
                        "/notify/pay",
                        "/notify/**",
                        // Swagger UI 相关路径
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui/index.html",
                        "/swagger-ui/index.html/**",
                        // 添加更多可能的路径
                        "/recruitment/page",
                        "/recruitment/info/*"
                );
        
        // 2. 角色权限校验拦截器 - 在 JWT 之后执行，检查 @RequireRole 注解
        // 拦截所有路径，由注解本身决定哪些方法需要校验
        registry.addInterceptor(roleCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // 排除不需要校验的路径
                        "/user/login",
                        "/user/register",
                        "/notify/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/recruitment/page",
                        "/recruitment/info/*"
                );
    }
}
