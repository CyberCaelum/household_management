package org.cybercaelum.household_management.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.context.BaseContext;
import org.cybercaelum.household_management.pojo.dto.RecruitmentDTO;
import org.cybercaelum.household_management.pojo.dto.RecruitmentPageDTO;
import org.cybercaelum.household_management.pojo.entity.PageResult;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.cybercaelum.household_management.pojo.vo.RecruitmentVO;
import org.cybercaelum.household_management.service.RecruitmentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 招募信息相关接口
 * @date 2025/11/9 下午1:23
 */
@RestController
@RequestMapping("/recruitment")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "招募服务",description = "招募服务")
@Validated
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    /**
     * @description 新增招募
     * @author CyberCaelum
     * @date 下午8:51 2025/11/10
     * @param recruitmentDTO 招募信息
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "新增招募",description = "新增招募")
    @PostMapping("/add")
    public Result addRecruitment(@Valid @RequestBody RecruitmentDTO recruitmentDTO) {
        log.info("新增招募：{}", recruitmentDTO);
        log.info("userId: {}", BaseContext.getUserId());
        recruitmentService.addRecruitment(recruitmentDTO);
        return Result.success();
    }
    /**
     * @description 修改招募状态
     * @author CyberCaelum
     * @date 下午3:51 2026/1/19
     * @param status 状态信息，0删除，1发布，2隐藏，3结束
     * @param recruitmentId 招募主键
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "修改招募状态",description = "修改招募状态")
    @PostMapping("/status/{status}")
    public Result updateRecruitmentStatus(@PathVariable int status, Long recruitmentId) {
        log.info("修改招募id: {},状态：{}", recruitmentId,status);
        recruitmentService.updateRecruitmentStatus(status,recruitmentId);
        return Result.success();
    }

    /**
     * @description 修改招募信息
     * @author CyberCaelum
     * @date 下午3:34 2026/1/20
     * @param recruitmentDTO 招募信息
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "修改招募信息",description = "修改招募信息")
    @PutMapping
    public Result updateRecruitment(@Valid @RequestBody RecruitmentDTO recruitmentDTO) {
        log.info("修改招募信息：{}", recruitmentDTO);
        recruitmentService.updateRecruitment(recruitmentDTO);
        return Result.success();
    }

    /**
     * @description 分页查询招募
     * @author CyberCaelum
     * @date 下午4:06 2026/1/20
     * @param recruitmentPageDTO 分页信息
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @GetMapping("/page")
    public Result pageRecruitment(RecruitmentPageDTO recruitmentPageDTO){
        log.info("分页查询招募信息：{}", recruitmentPageDTO);
        PageResult pageResult = recruitmentService.pageRecruitment(recruitmentPageDTO);
        return Result.success(pageResult);
    }
    //删除招募
    //根据薪资范围查询招募
    //根据开始结束时间查找招募
    //根据时间长度查找
    //城市查询
}
