package org.cybercaelum.household_management.interceptor;

import com.sun.jdi.LongValue;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.constant.JwtClaimsConstant;
import org.cybercaelum.household_management.context.BaseContext;
import org.cybercaelum.household_management.properties.JwtProperties;
import org.cybercaelum.household_management.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: jwt校验和拦截
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
     * @param request 请求信息
     * @param response 返回信息
     * @param handler 拦截的资源
     * @return boolean
     **/
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，放行
            return true;
        }
        //从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getUserTokenName());
        //校验令牌
        try {
            log.info("jwt令牌校验:{}",token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserTokenName(), token);
            //从jwt中获得用户id和用户的角色
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            Integer userRole = Integer.valueOf(claims.get(JwtClaimsConstant.USER_ROLE).toString());
            //将id和角色存入线程局部存储
            BaseContext.set(JwtClaimsConstant.USER_ID, userId);
            BaseContext.set(JwtClaimsConstant.USER_ROLE, userRole);
            //放行
            return true;
        } catch (Exception e) {
            //不通过，响应码401
            response.setStatus(401);
            return false;
        }
    }
}
















