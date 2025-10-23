package org.cybercaelum.household_management.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 阿里云配置类
 * @date 2025/10/23 下午9:07
 */
@Component
@ConfigurationProperties(prefix = "household.alioss")
@Data
public class AliOssProperties {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
}
