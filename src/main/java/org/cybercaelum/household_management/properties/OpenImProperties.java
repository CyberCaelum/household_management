package org.cybercaelum.household_management.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * OpenIM 配置属性
 * @author CyberCaelum
 * @version 1.0
 * @description: OpenIM 相关配置
 * @date 2026/3/1 下午3:00
 */
@Component
@ConfigurationProperties(prefix = "household.openim")
@Data
public class OpenImProperties {
    /**
     * OpenIM API 地址
     */
    private String apiAddress;

    /**
     * OpenIM 管理员密钥
     */
    private String secret;

    /**
     * OpenIM 管理员账号
     */
    private String adminUserId = "imAdmin";

    /**
     * 默认平台ID
     */
    private Integer defaultPlatformId = 5;
}
