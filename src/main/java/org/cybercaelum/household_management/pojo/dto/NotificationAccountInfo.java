package org.cybercaelum.household_management.pojo.dto;

import lombok.Data;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 添加机器人返回值
 * @date 2026/3/23
 */
@Data
public class NotificationAccountInfo {
    private String userID;
    private String faceURL;
    private String nickName;
    private Integer appMangerLevel;
}
