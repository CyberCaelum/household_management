package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 添加好友DTO
 * @date 2026/3/30 上午10:56
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ImportFriendDTO {
    private String ownerUserID;//指定用户ID
    private List<String> friendUserIDs;//指定的好友 ID 列表
}
