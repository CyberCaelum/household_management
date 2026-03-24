package org.cybercaelum.household_management.controller.admin;

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
 * @description: 员工管理控制器（仅管理员可访问）
 * @date: 2026/3/24
 */
@RestController
@RequestMapping("/admin/staff")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "员工管理", description = "平台员工账号管理（仅管理员）")
@RequireRole(RoleConstant.ADMIN)  // 整个控制器仅管理员可访问
public class AdminStaffController {

    @Operation(summary = "创建员工账号", description = "创建客服或管理员账号")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/create")
    public Result createStaff(@RequestParam String phoneNumber,
                               @RequestParam String username,
                               @RequestParam String password,
                               @RequestParam Integer role) {
        log.info("创建员工账号：phoneNumber={}, username={}, role={}", phoneNumber, username, role);
        // TODO: 调用 UserService 创建员工账号
        return Result.success();
    }

    @Operation(summary = "获取员工列表", description = "获取所有员工账号列表")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/list")
    public Result listStaff(@RequestParam(required = false) Integer role) {
        log.info("获取员工列表，role={}", role);
        // TODO: 调用 UserService 获取员工列表
        return Result.success();
    }

    @Operation(summary = "重置员工密码", description = "重置指定员工的登录密码")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/resetPassword/{staffId}")
    public Result resetPassword(@PathVariable Long staffId, 
                                 @RequestParam String newPassword) {
        log.info("重置员工 {} 的密码", staffId);
        // TODO: 调用 UserService 重置密码
        return Result.success();
    }

    @Operation(summary = "修改账号状态", description = "启用或禁用员工账号")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/status/{staffId}")
    public Result updateStatus(@PathVariable Long staffId, 
                                @RequestParam Integer status) {
        log.info("修改员工 {} 的状态为 {}", staffId, status);
        // TODO: 调用 UserService 修改状态
        return Result.success();
    }
}
