package org.cybercaelum.household_management.controller.callback;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.pojo.dto.OpenimUserCallbackDTO;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.cybercaelum.household_management.pojo.vo.OpenimCallbackVO;
import org.cybercaelum.household_management.service.OpenimUserCallBackService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: openim用户相关回调
 * @date 2026/3/28
 */
@RestController
@RequestMapping("/openim/callback/user")
@Slf4j
@RequiredArgsConstructor
public class OpenimUserCallBackController {

    private final OpenimUserCallBackService openimUserCallBackService;

    /**
     * @description openim用户在线状态回调
     * @author CyberCaelum
     * @date 2026/3/29
     * @param userCallbackDTO 用户信息
     * @return org.cybercaelum.household_management.pojo.vo.OpenimCallbackVO
     **/
    @Operation(summary = "用户登录openim回调",description = "用户登录openim回调")
    @PostMapping("/afterOnline")
    public OpenimCallbackVO afterOnline(@RequestBody OpenimUserCallbackDTO userCallbackDTO){
        log.info("用户登录openim回调：{}",userCallbackDTO);
        OpenimCallbackVO callbackVO = openimUserCallBackService.afterOnline(userCallbackDTO);
        return callbackVO;
    }

    /**
     * @description openim用户离线状态回调
     * @author CyberCaelum
     * @date 2026/3/29
     * @param userCallbackDTO 回调用户信息
     * @return org.cybercaelum.household_management.pojo.vo.OpenimCallbackVO
     **/
    @Operation(summary = "用户离线openim回调",description = "用户离线openim回调")
    @PostMapping("/afterOffline")
    public OpenimCallbackVO afterOffLine(@RequestBody OpenimUserCallbackDTO userCallbackDTO){
        log.info("用户离线openim回调：{}",userCallbackDTO);
        OpenimCallbackVO callbackVO = openimUserCallBackService.afterOffLine(userCallbackDTO);
        return callbackVO;
    }
}
