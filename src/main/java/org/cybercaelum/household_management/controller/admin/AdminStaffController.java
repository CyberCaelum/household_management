package org.cybercaelum.household_management.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.annotation.RequireRole;
import org.cybercaelum.household_management.constant.RoleConstant;
import org.cybercaelum.household_management.pojo.dto.CreateStaffDTO;
import org.cybercaelum.household_management.pojo.dto.StaffPageDTO;
import org.cybercaelum.household_management.pojo.entity.PageResult;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.cybercaelum.household_management.service.UserService;
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

    private final UserService userService;

    /**
     * @description 创建客服或管理员账号
     * @author CyberCaelum
     * @date 2026/3/24
     * @param createStaffDTO 账号信息
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @Operation(summary = "创建员工账号", description = "创建客服或管理员账号")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/create")
    public Result createStaff(@RequestBody CreateStaffDTO createStaffDTO) {
        log.info("创建员工账号：{}",createStaffDTO);
        userService.createStaff(createStaffDTO);
        return Result.success();
    }

    /**
     * @description 员工分页查询
     * @author CyberCaelum
     * @date 2026/3/24
     * @param staffPageDTO 筛选信息
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @Operation(summary = "员工分页查询", description = "员工分页查询")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/list")
    public Result<PageResult> pageStaff(StaffPageDTO staffPageDTO) {
        log.info("获取员工列表，staffPageDTO={}", staffPageDTO);
        PageResult pageResult = userService.pageStaff(staffPageDTO);
        return Result.success(pageResult);
    }

    /**
     * @description 重置员工密码
     * @author CyberCaelum
     * @date 2026/3/24
     * @param staffId 员工id
     * @param newPassword 新密码
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @Operation(summary = "重置员工密码", description = "重置指定员工的登录密码")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/resetPassword/{staffId}")
    public Result resetPassword(@PathVariable Long staffId, 
                                 @RequestParam String newPassword) {
        log.info("重置员工 {} 的密码", staffId);
        userService.resetPassword(staffId, newPassword);
        return Result.success();
    }

    /**
     * @description 修改账号状态
     * @author CyberCaelum
     * @date 2026/3/24
     * @param staffId 员工账号id
     * @param status 账号状态
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @Operation(summary = "修改账号状态", description = "启用或禁用员工账号")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/status/{staffId}")
    public Result updateStatus(@PathVariable Long staffId, 
                                @RequestParam Integer status) {
        log.info("修改员工 {} 的状态为 {}", staffId, status);
        userService.updateStatus(staffId, status);
        return Result.success();
    }
}
