package org.cybercaelum.household_management.controller.customer_service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.annotation.RequireRole;
import org.cybercaelum.household_management.constant.RoleConstant;
import org.cybercaelum.household_management.context.BaseContext;
import org.cybercaelum.household_management.exception.BaseException;
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
    
    @Operation(summary = "请求客服", description = "用户请求分配客服，如果无可用客服则进入排队")
    @SecurityRequirement(name = "bearerAuth")
    @RequireRole(RoleConstant.USER)
    @PostMapping("/request")
    public Result requestCustomerService() {
        Long userId = BaseContext.getUserId();
        log.info("用户 {} 请求客服", userId);
        CsGroupAssignmentResult result = customerServiceService.requestCustomerService(userId);
        return Result.success(result);
    }
    
    @Operation(summary = "获取排队位置", description = "获取用户当前在等待队列中的位置")
    @SecurityRequirement(name = "bearerAuth")
    @RequireRole(RoleConstant.USER)
    @GetMapping("/waiting-position")
    public Result getWaitingPosition() {
        Long userId = BaseContext.getUserId();
        int position = customerServiceService.getPosition(userId);
        return Result.success(position);
    }

    @Operation(summary = "结束会话", description = "用户或客服主动结束会话")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/session/end")
    public Result endSession(@RequestBody SessionEndDTO sessionEndDTO) {
        log.info("结束会话sessionEndDTO: {}", sessionEndDTO);
        customerServiceService.toEndSession(sessionEndDTO);
        return Result.success();
    }

    @Operation(summary = "获取当前会话", description = "获取用户当前的客服会话信息")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/session/current")
    public Result getCurrentSession() {
        Long userId = BaseContext.getUserId();
        Map<String, String> session = customerServiceService.getUserSession(userId);
        if (session == null) {
            throw new BaseException("会话不存在");
        }
        //返回群组信息
        return Result.success("cs_" + session.get("userId"));
    }

}
