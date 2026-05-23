package org.cybercaelum.household_management.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 机器人账号配置
 * @date 2026/5/23 上午10:35
 */
@Component
@ConfigurationProperties(prefix = "household.robot")
@Data
public class RobotProperties {
    /**
     * 机器人账号ID
     */
    private Long id = 24L;
    /**
     * 机器人账号ID2
     */
    private Long id2 = 25L;
}
