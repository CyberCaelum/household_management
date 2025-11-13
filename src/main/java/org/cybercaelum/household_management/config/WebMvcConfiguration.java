package org.cybercaelum.household_management.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.interceptor.JwtTokenInterceptor;
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
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        //用户端拦截器
        registry.addInterceptor(jwtTokenInterceptor)
                .addPathPatterns("/user/**")
                .addPathPatterns("/recruitment/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
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
                        "/favicon.ico",
                        "/error",
                        "/csrf"
                );
    }
}
