package org.cybercaelum.household_management.controller.common;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.cybercaelum.household_management.service.CommonService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 通用接口
 * @date 2025/10/23 下午7:05
 */
@RequestMapping
@RestController
@RequiredArgsConstructor
@Slf4j
public class CommonController {
    private final CommonService commonService;

    /**
     * @description 文件上传
     * @author CyberCaelum
     * @date 下午8:56 2025/10/30
     * @param file 目标文件
     * @return org.cybercaelum.household_management.pojo.entity.Result<java.lang.String>
     **/
    @Operation(summary = "文件上传",description = "文件上传")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传");
        String string = commonService.upload(file);
        return Result.success(string);
    }
}
