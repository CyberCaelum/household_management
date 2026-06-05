package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.LifecycleState;

import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 邀请用户进群
 * @date 2026/6/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InviteUserToGroupDTO {
    private String groupId; //群ID
    private List<String> invitedUserIDs; //被邀请的用户ID列表
    @Builder.Default
    private String reason = "客服进群"; //邀请说明
}
