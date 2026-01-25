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
import org.cybercaelum.household_management.service.ResumeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @description 新增简介
     * @author CyberCaelum
     * @date 下午7:43 2026/1/25
     * @param resumeDTO 简介信息
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/add")
    @Operation(summary = "新增简介",description = "新增简介")
    public Result addResume(@Valid ResumeDTO resumeDTO) {
        log.info("新增简介：{}", resumeDTO);
        resumeService.addResume(resumeDTO);
        return Result.success();
    }
}
