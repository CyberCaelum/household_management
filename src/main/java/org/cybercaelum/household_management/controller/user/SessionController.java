package org.cybercaelum.household_management.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.pojo.dto.SessionCreateDTO;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.cybercaelum.household_management.pojo.vo.SessionCreateVO;
import org.cybercaelum.household_management.service.SessionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 会话控制器
 * @date 2026/3/1
 */
@RestController
@RequestMapping("/session")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "会话服务", description = "会话管理相关接口")
@Validated
public class SessionController {

    private final SessionService sessionService;

    /**
     * @description 创建或获取会话
     * 用户点击招募信息的"联系雇主"按钮时调用此接口
     * @author CyberCaelum
     * @date 2026/3/1
     * @param sessionCreateDTO 创建会话请求
     * @return org.cybercaelum.household_management.pojo.entity.Result<org.cybercaelum.household_management.pojo.vo.SessionCreateVO>
     **/
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "创建或获取会话", description = "用户点击私聊时调用，传入招募ID，返回会话信息（包括OpenIM会话ID）")
    @PostMapping("/create")
    public Result<SessionCreateVO> createOrGetSession(@Valid @RequestBody SessionCreateDTO sessionCreateDTO) {
        log.info("创建或获取会话请求：recruitmentId={}", sessionCreateDTO.getRecruitmentId());
        SessionCreateVO sessionVO = sessionService.createOrGetSession(sessionCreateDTO);
        return Result.success(sessionVO);
    }

    /**
     * @description 获取当前用户的会话列表
     * @author CyberCaelum
     * @date 2026/3/1
     * @return org.cybercaelum.household_management.pojo.entity.Result<java.util.List<org.cybercaelum.household_management.pojo.vo.SessionCreateVO>>
     **/
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "获取会话列表", description = "获取当前登录用户的所有活动会话列表")
    @GetMapping("/list")
    public Result<List<SessionCreateVO>> getSessionList() {
        log.info("获取当前用户会话列表");
        List<SessionCreateVO> sessions = sessionService.getUserSessions();
        return Result.success(sessions);
    }

    /**
     * @description 关闭会话
     * @author CyberCaelum
     * @date 2026/3/1
     * @param sessionId 会话ID
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "关闭会话", description = "关闭指定的会话")
    @PostMapping("/close/{sessionId}")
    public Result closeSession(@PathVariable Long sessionId) {
        log.info("关闭会话请求：sessionId={}", sessionId);
        sessionService.closeSession(sessionId);
        return Result.success();
    }
}
