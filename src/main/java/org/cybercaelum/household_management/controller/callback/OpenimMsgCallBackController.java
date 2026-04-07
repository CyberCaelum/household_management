package org.cybercaelum.household_management.controller.callback;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.pojo.dto.MessageCallbackDTO;
import org.cybercaelum.household_management.pojo.dto.OpenimCallbackDTO;
import org.cybercaelum.household_management.service.CustomerServiceService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: openim消息相关回调
 * @date 2026/3/30
 */
@RestController
@RequestMapping("/openim/callback")
@Slf4j
@RequiredArgsConstructor
public class OpenimMsgCallBackController {

    private final CustomerServiceService customerServiceService;

    @Operation(summary = "用户发送消息openim回调",description = "用户发送消息openim回调")
    @PostMapping("/callbackAfterSendGroupMsgCommand")
    public OpenimCallbackDTO afterSendGroupMsg(@RequestBody MessageCallbackDTO messageCallbackDTO){
        log.info("用户发送消息openim回调messageCallbackDTO = {}", messageCallbackDTO);
        OpenimCallbackDTO openimCallbackDTO = customerServiceService.freshCsGroup(messageCallbackDTO);
        return openimCallbackDTO;
    }
}
