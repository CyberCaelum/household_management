package org.cybercaelum.household_management.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: jwt令牌相关配置
 * @date 2025/10/16 下午7:04
 */
@Component
@ConfigurationProperties(prefix = "household.jwt")
@Data
public class JwtProperties {
    //密钥
    private String userSecretKey;
    //过期时间
    private Long userTtl;
    //令牌名字
    private String userTokenName;
}
