package org.cybercaelum.household_management.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.pojo.dto.ResumeDTO;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.cybercaelum.household_management.pojo.entity.Resume;
import org.cybercaelum.household_management.pojo.vo.ResumeVO;
import org.cybercaelum.household_management.service.ResumeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 简介controller
 * @date 2026/1/23 下午3:49
 */
@RestController
@RequestMapping("/resume")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "简介服务",description = "简介服务")
@Validated
public class ResumeController {

    private final ResumeService resumeService;

    /**
     * @description 新增简历
     * @author CyberCaelum
     * @date 下午7:43 2026/1/25
     * @param resumeDTO 简介信息
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/add")
    @Operation(summary = "新增简历",description = "新增简历")
    public Result addResume(@Valid ResumeDTO resumeDTO) {
        log.info("新增简历：{}", resumeDTO);
        resumeService.addResume(resumeDTO);
        return Result.success();
    }

    //增删改查
    /**
     * @description 根据用户id查询简历
     * @author CyberCaelum
     * @date 下午7:03 2026/1/26
     * @param id 用户id
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @Operation(summary = "根据用户id查找简历",description = "根据用户id查找简历")
    @GetMapping("/{id}")
    public Result getResume(@PathVariable Long id) {
        log.info("根据用户id查找简历信息：{}",id);
        ResumeVO resumeVO = resumeService.getResume(id);
        return Result.success(resumeVO);
    }

    /**
     * @description 修改简历信息
     * @author CyberCaelum
     * @date 2026/2/18
     * @param resumeDTO 简历信息
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/update")
    @Operation(summary = "修改简历",description = "修改简历信息")
    public Result updateResume(@Valid @RequestBody ResumeDTO resumeDTO) {
        log.info("修改简历：{}", resumeDTO);
        resumeService.updateResume(resumeDTO);
        return Result.success();
    }

    /**
     * @description 修改简历可见性状态
     * @author CyberCaelum
     * @date 2026/2/18
     * @param visibility 可见性，0为不可见，1为可见
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/visibility/{visibility}")
    @Operation(summary = "修改简历可见性",description = "修改简历可见性状态，0为不可见，1为可见")
    public Result updateVisibility(@PathVariable Integer visibility) {
        log.info("修改简历可见性：{}", visibility);
        resumeService.updateVisibility(visibility);
        return Result.success();
    }

}
