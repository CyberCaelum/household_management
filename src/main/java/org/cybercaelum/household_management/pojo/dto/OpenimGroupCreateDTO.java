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
        @Builder.Default
        private Integer groupType = 2;        // 固定为 2
        @Builder.Default
        private Integer needVerification = 1; // 入群验证方式，0：申请加入群需要同意，成员邀请可直接进群；1：所有人进群需要验证，除了群主管理员邀请进群；2：直接进群
        @Builder.Default
        private Integer lookMemberInfo = 1;   // 成员信息查看权限，0：允许查看群成员信息；1：不允许查看群成员信息
        @Builder.Default
        private Integer applyMemberFriend = 1;// 申请成员好友权限，0：允许从群成员处添加好友；1：不允许添加
    }
}
