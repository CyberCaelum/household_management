package org.cybercaelum.household_management.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.context.BaseContext;
import org.cybercaelum.household_management.pojo.dto.RecruitmentDTO;
import org.cybercaelum.household_management.pojo.entity.Recruitment;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.cybercaelum.household_management.service.RecruitmentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        log.info("userId{}", BaseContext.getUserId());
        recruitmentService.addRecruitment(recruitmentDTO);
        return Result.success();
    }
    //修改招募状态
    //修改招募
    //招募分页查询
    //删除招募
    //根据薪资范围查询招募
    //根据开始结束时间查找招募
    //根据时间长度查找
    //城市查询
}
