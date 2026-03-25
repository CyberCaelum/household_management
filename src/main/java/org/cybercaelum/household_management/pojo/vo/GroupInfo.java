package org.cybercaelum.household_management.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 群组创建返回值
 * @date 2026/3/25 上午8:20
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GroupInfo {
    private String groupID;
    private String groupName;
    private String notification;
    private String introduction;
    private String faceURL;
    private String ownerUserID;
    private Long createTime;          // 时间戳，使用 Long
    private Integer memberCount;      // 使用包装类，避免默认值问题
    private String ex;
    private Integer status;
    private String creatorUserID;
    private Integer groupType;
    private Integer needVerification;
    private Integer lookMemberInfo;
    private Integer applyMemberFriend;
    private Long notificationUpdateTime;
    private String notificationUserID;
}
