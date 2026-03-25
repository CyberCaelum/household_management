package org.cybercaelum.household_management.controller.customer_service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.annotation.RequireRole;
import org.cybercaelum.household_management.constant.RoleConstant;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.springframework.web.bind.annotation.*;

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
@RequireRole({RoleConstant.ADMIN, RoleConstant.CUSTOMER_SERVICE})  // 类级别注解：该控制器下所有方法都需要管理员或客服角色
public class CustomerServiceController {

    @Operation(summary = "获取待处理争议列表", description = "获取当前客服待处理的争议工单")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/disputes/pending")
    public Result getPendingDisputes() {
        log.info("获取待处理争议列表");
        // TODO: 实现业务逻辑
        return Result.success();
    }

    @Operation(summary = "获取客服统计", description = "获取客服工作统计数据")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/statistics")
    public Result getStatistics() {
        log.info("获取客服统计");
        // TODO: 实现业务逻辑
        return Result.success();
    }

    @Operation(summary = "分配争议给客服", description = "将争议工单分配给指定客服")
    @SecurityRequirement(name = "bearerAuth")
    @RequireRole(RoleConstant.ADMIN)  // 方法级别覆盖类级别：仅管理员可分配
    @PostMapping("/disputes/assign/{disputeId}")
    public Result assignDispute(@PathVariable Long disputeId, 
                                 @RequestParam Long kefuId) {
        log.info("分配争议 {} 给客服 {}", disputeId, kefuId);
        // TODO: 实现业务逻辑
        return Result.success();
    }
}
