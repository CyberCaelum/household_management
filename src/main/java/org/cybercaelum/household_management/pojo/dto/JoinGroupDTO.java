package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 邀请指定用户到指定群组
 * @date 2026/4/1 上午8:51
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JoinGroupDTO {
    private String groupID;//群ID
    private String reqMessage;//申请信息
    private Integer joinSource;//加群来源，1：管理员邀请，2：被邀请，3：搜索加入，4：扫码加入
    private String inviterUserID;//申请者ID
}
