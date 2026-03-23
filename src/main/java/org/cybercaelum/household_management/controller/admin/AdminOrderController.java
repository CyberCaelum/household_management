package org.cybercaelum.household_management.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.pojo.dto.OrdersCancelDTO;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.cybercaelum.household_management.service.OrderService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 管理员订单Controller（平台端）
 * @date 2026/3/15
 */
@RestController
@RequestMapping("/admin/order")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "平台订单管理", description = "平台订单管理相关接口")
@Validated
public class AdminOrderController {

    private final OrderService orderService;

    /**
     * @description 平台取消订单
     * @author CyberCaelum
     * @date 2026/3/15
     * @param ordersCancelDTO 取消信息
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @Operation(summary = "平台取消订单", description = "平台强制取消订单")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/cancel")
    public Result adminCancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("平台取消订单，orderId: {}", ordersCancelDTO.getId());
        orderService.adminCancel(ordersCancelDTO);
        return Result.success();
    }

    /**
     * @description 平台裁决取消申请
     * @author CyberCaelum
     * @date 2026/3/15
     * @param applicationId 申请ID
     * @param decision 裁决结果：1-同意取消，2-拒绝取消，3-部分结算
     * @param note 平台备注
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @Operation(summary = "平台裁决", description = "平台对取消申请进行裁决")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/platformDecide/{applicationId}")
    public Result platformDecide(@PathVariable Long applicationId,
                                  @RequestParam Integer decision,
                                  @RequestParam(required = false) String note) {
        log.info("平台裁决取消申请，applicationId: {}，decision: {}，note: {}", applicationId, decision, note);
        orderService.platformDecideCancelApplication(applicationId, decision, note);
        return Result.success();
    }
}
