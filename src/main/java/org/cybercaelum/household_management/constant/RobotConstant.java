package org.cybercaelum.household_management.constant;

import jakarta.annotation.PostConstruct;
import org.cybercaelum.household_management.properties.RobotProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 机器人账号常量
 * @date 2026/4/8 下午2:42
 */
@Component
public class RobotConstant {
    public static Long id;
    public static Long id2;

    @Autowired
    private RobotProperties robotProperties;

    @PostConstruct
    public void init() {
        id = robotProperties.getId();
        id2 = robotProperties.getId2();
    }
}
