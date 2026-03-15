package org.cybercaelum.household_management.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.context.BaseContext;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.cybercaelum.household_management.service.OpenImService;
import org.cybercaelum.household_management.service.impl.OpenImUserTokenService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: OpenIM 控制器
 * @date 2026/3/15
 */
@RestController
@RequestMapping("/openim")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "OpenIM服务", description = "OpenIM 相关接口")
@Validated
public class OpenImController {

    private final OpenImService openImService;
    private final OpenImUserTokenService openImUserTokenService;

    /**
     * @description 刷新 OpenIM 用户 Token
     * 前端在 Token 即将过期或已过期时调用，获取新的用户 Token
     * @author CyberCaelum
     * @date 2026/3/15
     * @return org.cybercaelum.household_management.pojo.entity.Result<java.lang.String>
     **/
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "刷新 OpenIM Token", 
               description = "刷新当前登录用户的 OpenIM Token，用于 Token 即将过期或已过期时")
    @PostMapping("/refresh-token")
    public Result<String> refreshToken() {
        Long userId = BaseContext.getUserId();
        log.info("用户 {} 请求刷新 OpenIM Token", userId);
        
        String userIdStr = String.valueOf(userId);
        String newToken = openImUserTokenService.refreshUserToken(userIdStr);
        
        log.info("用户 {} OpenIM Token 刷新成功", userId);
        return Result.success(newToken);
    }
}
