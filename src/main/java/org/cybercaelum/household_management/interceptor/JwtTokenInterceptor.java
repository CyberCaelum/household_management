package org.cybercaelum.household_management.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.properties.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: TODO
 * @date 2025/10/16 下午7:03
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class JwtTokenInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;

    /**
     * @description 校验jwt
     * @author CyberCaelum
     * @date 下午9:14 2025/10/16
     * @param request
     * @param response
     * @param handler
     * @return boolean
     **/
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

    }
}
