package org.cybercaelum.household_management.controller.callback;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Operation(summary = "用户登录openim回调",description = "用户登录openim回调")
    @PostMapping("/afterOnline")
    public Result afterOnline(){
        //TODO 接受回调后将客服登录
        return Result.success();
    }
}
