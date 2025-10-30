package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.constant.MessageConstant;
import org.cybercaelum.household_management.service.CommonService;
import org.cybercaelum.household_management.utils.AliOssUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 通用接口服务层
 * @date 2025/10/23 下午8:57
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {
    private final AliOssUtil aliOssUtil;

    /**
     * @description 文件上传
     * @author CyberCaelum
     * @date 下午7:32 2025/10/30
     * @param file 目标文件
     * @return java.lang.String
     **/
    public String upload(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID().toString() + extension;
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return filePath;
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return MessageConstant.UPLOAD_FAILED;
    }

}
