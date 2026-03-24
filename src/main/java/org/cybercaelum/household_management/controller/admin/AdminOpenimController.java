package org.cybercaelum.household_management.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.pojo.dto.OpenimBootAddDTO;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.cybercaelum.household_management.service.OpenimBootService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 管理员openim
 * @date 2026/3/23
 */
@RestController
@RequestMapping("/admin/openim")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "平台聊天系统管理", description = "平台聊天系统管理相关接口")
@Validated
public class AdminOpenimController {

    private final OpenimBootService openimBootService;

    /**
     * @description 新增机器人账号
     * @author CyberCaelum
     * @date 2026/3/23
     * @param openimBootAddDTO 机器人账号信息
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/add_boot")
    @Operation(summary = "新增机器人", description = "新增机器人")
    public Result addBoot(@RequestBody OpenimBootAddDTO openimBootAddDTO){
        log.info("openim boot addDTO:{}", openimBootAddDTO);
        openimBootService.addBoot(openimBootAddDTO);
        return Result.success();
    }
    //添加客服账号
    //
}
