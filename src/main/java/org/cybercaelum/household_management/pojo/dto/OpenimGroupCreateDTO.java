package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 群组创建
 * @date 2026/3/25 上午8:19
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OpenimGroupCreateDTO {

    private List<String> memberUserIDs;   // 成员用户 ID 列表
    private List<String> adminUserIDs;    // 管理员用户 ID 列表
    private String ownerUserID;           // 群主用户 ID

    private GroupInfo groupInfo;          // 群信息

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GroupInfo {
        private String groupID;           // 群 ID
        private String groupName;         // 群名称
        private String notification;      // 群公告
        private String introduction;      // 群简介
        private String faceURL;           // 群头像 URL
        private String ex;                // 扩展字段
        private Integer groupType;        // 群类型（例如 2 表示大群）
        private Integer needVerification; // 入群验证方式（0 表示不需要验证）
        private Integer lookMemberInfo;   // 成员信息查看权限
        private Integer applyMemberFriend;// 申请成员好友权限
    }
}
