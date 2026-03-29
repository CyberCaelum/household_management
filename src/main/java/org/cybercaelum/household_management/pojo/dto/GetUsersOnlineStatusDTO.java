package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 查询用户在线状态DTO
 * @date 2026/3/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUsersOnlineStatusDTO {
    private List<String> userIDs;//用户id列表
}
