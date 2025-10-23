package org.cybercaelum.household_management.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.properties.AliOssProperties;
import org.cybercaelum.household_management.utils.AliOssUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 创建阿里云文件上传工具类对象
 * @date 2025/10/23 下午9:05
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class OssConfiguration {

    private final AliOssProperties aliOssProperties;

    /**
     * @description 创建阿里云文件上传工具类对象
     * @author CyberCaelum
     * @date 下午9:15 2025/10/23
     * @return org.cybercaelum.household_management.utils.AliOssUtil
     **/
    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil() {
        log.info("创建阿里云文件上传工具类对象");
        return new AliOssUtil(aliOssProperties.getEndpoint(),aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),aliOssProperties.getBucketName());
    }

}
