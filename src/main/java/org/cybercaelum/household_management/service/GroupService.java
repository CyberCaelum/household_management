package org.cybercaelum.household_management.service;

import org.cybercaelum.household_management.pojo.dto.GroupCreateDTO;
import org.cybercaelum.household_management.pojo.vo.GroupInfo;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 群组服务接口
 * @date 2026/3/25 上午8:50
 */
public interface GroupService {
    GroupInfo createGroup(GroupCreateDTO groupCreateDTO);

    GroupInfo createPrivateChat(GroupCreateDTO groupCreateDTO);
}
