package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cybercaelum.household_management.pojo.vo.GroupInfo;

import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 群组信息列表
 * @date 2026/3/30
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupListDTO {
    private List<GroupInfo> groupInfos;
}
