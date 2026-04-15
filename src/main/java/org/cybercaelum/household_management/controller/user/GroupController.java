package org.cybercaelum.household_management.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.annotation.RequireRole;
import org.cybercaelum.household_management.constant.RoleConstant;
import org.cybercaelum.household_management.pojo.dto.GroupCreateDTO;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.cybercaelum.household_management.pojo.vo.GroupInfo;
import org.cybercaelum.household_management.service.GroupService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 群组相关服务
 * @date 2026/3/25 上午8:10
 */
@RestController
@RequestMapping("/group")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "群组相关服务",description = "群组相关服务")
@Validated
public class GroupController {

    private final GroupService groupService;

    //创建私聊
    /**
     * @description 创建私聊
     * @author CyberCaelum
     * @date 2026/3/30
     * @param groupCreateDTO 私聊信息
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @RequireRole(RoleConstant.USER)
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/create/private_chat")
    @Operation(summary = "创建私聊",description = "创建私聊")
    public Result creatPrivateChat(@RequestBody GroupCreateDTO groupCreateDTO) {
        log.info("创建私聊groupCreateDTO = {}", groupCreateDTO);
        GroupInfo groupInfo = groupService.createPrivateChat(groupCreateDTO);
        return Result.success(groupInfo);
    }

    /**
     * @description 创建客服群组
     * @author CyberCaelum
     * @date 2026/3/31
     * @param groupCreateDTO 群组信息
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/create/cs_chat")
    @Operation(summary = "创建客服聊天群组", description = "创建用户与客服的聊天群组")
    public Result createCsChat(@RequestBody GroupCreateDTO groupCreateDTO) {
        log.info("创建客服聊天groupCreateDTO = {}", groupCreateDTO);
        GroupInfo groupInfo = groupService.createCsChat(groupCreateDTO);
        return Result.success(groupInfo);
    }

}
