package org.cybercaelum.household_management.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: RocketMQ配置
 * @date 2026/3/17
 */
@Data
@Component
@ConfigurationProperties(prefix = "household.rocketmq")
public class RocketMQProperties {
    /**
     * Proxy 地址
     */
    private String endpoint;
    /**
     * 发送者组
     */
    private String group;
}
