package org.cybercaelum.household_management.controller.common;

import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.cybercaelum.household_management.service.CommonService;
import org.cybercaelum.household_management.utils.AliOssUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;

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
    private final AliOssUtil aliOssUtil;

    /**
     * @description 文件上传
     * @author CyberCaelum
     * @date 下午8:56 2025/10/30
     * @param file 目标文件
     * @return org.cybercaelum.household_management.pojo.entity.Result<java.lang.String>
     **/
    @Operation(summary = "文件上传", description = "文件上传")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> upload(@RequestPart("file") MultipartFile file) {
        log.info("文件上传");
        String string = commonService.upload(file);
        return Result.success(string);
    }

    /**
     * @description 图片查看（OSS私有Bucket代理访问）
     * @author CyberCaelum
     * @date 下午9:15 2025/10/23
     * @param objectName 图片文件名
     * @param response HTTP响应
     **/
    @Operation(summary = "图片查看", description = "OSS私有图片代理访问，支持浏览器缓存")
    @GetMapping("/image/{objectName}")
    @Parameter(name = "objectName", description = "图片文件名")
    public void viewImage(@PathVariable String objectName, HttpServletResponse response) {
        try (OSSObject ossObject = aliOssUtil.getObject(objectName)) {
            String contentType = ossObject.getObjectMetadata().getContentType();
            if (contentType != null) {
                response.setContentType(contentType);
            } else {
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            }

            // 浏览器缓存1小时，减轻后端压力
            response.setHeader("Cache-Control", "max-age=3600");

            try (InputStream in = ossObject.getObjectContent();
                 OutputStream out = response.getOutputStream()) {
                in.transferTo(out);
                out.flush();
            }
        } catch (OSSException e) {
            if ("NoSuchKey".equals(e.getErrorCode())) {
                log.warn("图片不存在: {}", objectName);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                log.error("OSS访问异常, Error Message:{}, Error Code:{}", e.getErrorMessage(), e.getErrorCode());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("图片代理读取失败: {}", objectName, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
