package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.pojo.dto.GroupCreateDTO;
import org.cybercaelum.household_management.pojo.vo.GroupCreateVO;
import org.cybercaelum.household_management.service.GroupService;
import org.springframework.stereotype.Service;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 群组服务类
 * @date 2026/3/25 上午8:50
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    @Override
    public GroupCreateVO createGroup(GroupService groupService) {
        //群主为机器人
        GroupCreateDTO groupCreateDTO = new GroupCreateDTO();
        //群ID为拼接发起人ID_招募id_接受人id
        //前端需要将群两个人的名字头像信息存入数据库，
        //后端创建私聊需要完整的权限校验和权限限制
        //然后监听onGroupMemberInfoChanged回调，当发生改变的时候修改前端数据库，然后刷新界面信息
        return null;
    }
}
