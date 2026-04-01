package org.cybercaelum.household_management.controller.customer_service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.annotation.RequireRole;
import org.cybercaelum.household_management.constant.RoleConstant;
import org.cybercaelum.household_management.context.BaseContext;
import org.cybercaelum.household_management.feign.OpenimFeignClient;
import org.cybercaelum.household_management.pojo.dto.CsGroupAssignmentResult;
import org.cybercaelum.household_management.pojo.dto.JoinGroupDTO;
import org.cybercaelum.household_management.pojo.dto.SessionEndDTO;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.cybercaelum.household_management.service.CustomerServiceService;
import org.cybercaelum.household_management.service.OpenImService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 客服工作台控制器
 * @date: 2026/3/24
 */
@RestController
@RequestMapping("/kefu")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "客服工作台", description = "客服相关工作台接口")
public class CustomerServiceController {

    private final CustomerServiceService customerServiceService;
    private final OpenimFeignClient openimFeignClient;
    private final OpenImService openImService;

    @Operation(summary = "获取待处理争议列表", description = "获取当前客服待处理的争议工单")
    @SecurityRequirement(name = "bearerAuth")
    @RequireRole({RoleConstant.ADMIN, RoleConstant.CUSTOMER_SERVICE})
    @GetMapping("/disputes/pending")
    public Result getPendingDisputes() {
        log.info("获取待处理争议列表");
        // TODO: 实现业务逻辑
        return Result.success();
    }

    @Operation(summary = "获取客服统计", description = "获取客服工作统计数据")
    @SecurityRequirement(name = "bearerAuth")
    @RequireRole({RoleConstant.ADMIN, RoleConstant.CUSTOMER_SERVICE})
    @GetMapping("/statistics")
    public Result getStatistics() {
        log.info("获取客服统计");
        // TODO: 实现业务逻辑
        return Result.success();
    }

    @Operation(summary = "分配争议给客服", description = "将争议工单分配给指定客服")
    @SecurityRequirement(name = "bearerAuth")
    @RequireRole(RoleConstant.ADMIN)
    @PostMapping("/disputes/assign/{disputeId}")
    public Result assignDispute(@PathVariable Long disputeId, 
                                 @RequestParam Long kefuId) {
        log.info("分配争议 {} 给客服 {}", disputeId, kefuId);
        // TODO: 实现业务逻辑
        return Result.success();
    }
    
    // ==================== 客服聊天相关接口 ====================
    
    @Operation(summary = "请求客服", description = "用户请求分配客服，如果无可用客服则进入排队")
    @SecurityRequirement(name = "bearerAuth")
    @RequireRole(RoleConstant.USER)
    @PostMapping("/request")
    public Result requestCustomerService() {
        Long userId = BaseContext.getUserId();
        log.info("用户 {} 请求客服", userId);
        
        CsGroupAssignmentResult result = customerServiceService.createCsGroup(userId);
        
        switch (result.getStatus()) {
            case SUCCESS:
                return Result.success(result);
            case SESSION_EXISTS:
                return Result.success(result);
            case NO_AVAILABLE_CS:
                customerServiceService.addToWaitingQueue(userId);
                int position = customerServiceService.getWaitingPosition(userId);
                return Result.success(Map.of(
                        "status", "WAITING",
                        "position", position,
                        "message", result.getMessage()
                ));
            default:
                return Result.error(result.getMessage());
        }
    }
    
    @Operation(summary = "获取排队位置", description = "获取用户当前在等待队列中的位置")
    @SecurityRequirement(name = "bearerAuth")
    @RequireRole(RoleConstant.USER)
    @GetMapping("/waiting-position")
    public Result getWaitingPosition() {
        Long userId = BaseContext.getUserId();
        int position = customerServiceService.getWaitingPosition(userId);
        
        if (position == -1) {
            // 检查是否已有会话
            Map<String, String> session = customerServiceService.getUserSession(userId);
            if (session != null) {
                return Result.success(Map.of(
                        "status", "IN_SESSION",
                        "csId", session.get("csId")
                ));
            }
            return Result.success(Map.of("status", "NOT_IN_QUEUE"));
        }
        
        return Result.success(Map.of(
                "status", "WAITING",
                "position", position
        ));
    }
    //TODO 没有判断权限，当前用户是否有权结束指定会话
    @Operation(summary = "结束会话", description = "用户或客服主动结束会话")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/session/end")
    public Result endSession(@RequestBody SessionEndDTO sessionEndDTO) {
        Long currentUserId = BaseContext.getUserId();
        
        // 如果用户没有提供userId，使用当前用户ID
        if (sessionEndDTO.getUserId() == null) {
            sessionEndDTO.setUserId(currentUserId);
        }
        
        // 判断是用户结束还是会话结束
        if (sessionEndDTO.getReason() == null) {
            // 根据当前角色判断
            if (currentUserId.equals(sessionEndDTO.getUserId())) {
                sessionEndDTO.setReason(SessionEndDTO.REASON_USER_INITIATED);
            } else {
                sessionEndDTO.setReason(SessionEndDTO.REASON_CS_INITIATED);
            }
        }
        
        log.info("结束会话：userId={}, reason={}", sessionEndDTO.getUserId(), sessionEndDTO.getReason());
        
        boolean success = customerServiceService.endSession(sessionEndDTO);
        if (success) {
            return Result.success();
        } else {
            return Result.error("会话不存在或已结束");
        }
    }
    //TODO 缺少返回groupId
    @Operation(summary = "获取当前会话", description = "获取用户当前的客服会话信息")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/session/current")
    public Result getCurrentSession() {
        Long userId = BaseContext.getUserId();
        Map<String, String> session = customerServiceService.getUserSession(userId);
        
        if (session == null) {
            return Result.success(Map.of("hasSession", false));
        }
        
        return Result.success(Map.of(
                "hasSession", true,
                "csId", session.get("csId"),
                "createTime", session.get("createTime"),
                "lastActiveTime", session.get("lastActiveTime")
        ));
    }
    
    @Operation(summary = "客服结束会话", description = "客服结束与指定用户的会话")
    @SecurityRequirement(name = "bearerAuth")
    @RequireRole({RoleConstant.ADMIN, RoleConstant.CUSTOMER_SERVICE})
    @PostMapping("/session/end/{userId}")
    public Result endSessionByCs(@PathVariable Long userId) {
        Long csId = BaseContext.getUserId();
        log.info("客服 {} 结束与用户 {} 的会话", csId, userId);
        
        SessionEndDTO sessionEndDTO = SessionEndDTO.builder()
                .userId(userId)
                .csId(csId)
                .reason(SessionEndDTO.REASON_CS_INITIATED)
                .build();
        
        boolean success = customerServiceService.endSession(sessionEndDTO);
        if (success) {
            return Result.success();
        } else {
            return Result.error("会话不存在或已结束");
        }
    }
}
