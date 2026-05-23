package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 获取群组信息DTO
 * @date 2026/5/23
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GroupInfoGetDTO {
    private List<String> groupIDs;
}
