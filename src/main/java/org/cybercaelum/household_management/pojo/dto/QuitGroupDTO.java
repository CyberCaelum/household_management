package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 让某个群成员退出指定群组
 * @date 2026/4/1 上午9:06
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuitGroupDTO {
    private String groupID;//群组id
    private String userID;//用户id
}
