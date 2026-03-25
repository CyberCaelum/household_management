package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.constant.GroupTypeConstant;
import org.cybercaelum.household_management.context.BaseContext;
import org.cybercaelum.household_management.exception.OpenimRequestErrorException;
import org.cybercaelum.household_management.exception.RecruitmentNotFoundException;
import org.cybercaelum.household_management.exception.RoleErrorException;
import org.cybercaelum.household_management.exception.UserNotFoundException;
import org.cybercaelum.household_management.feign.OpenimFeignClient;
import org.cybercaelum.household_management.mapper.OpenimGroupMapper;
import org.cybercaelum.household_management.mapper.RecruitmentMapper;
import org.cybercaelum.household_management.mapper.UserMapper;
import org.cybercaelum.household_management.pojo.dto.GroupCreateDTO;
import org.cybercaelum.household_management.pojo.dto.OpenimGroupCreateDTO;
import org.cybercaelum.household_management.pojo.entity.OpenimGroup;
import org.cybercaelum.household_management.pojo.entity.OpenimResult;
import org.cybercaelum.household_management.pojo.entity.Recruitment;
import org.cybercaelum.household_management.pojo.entity.User;
import org.cybercaelum.household_management.pojo.vo.GroupInfo;
import org.cybercaelum.household_management.service.GroupService;
import org.cybercaelum.household_management.service.OpenImService;
import org.cybercaelum.household_management.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private final OpenImService openImService;
    private final OpenimGroupMapper openimGroupMapper;
    private final RecruitmentMapper recruitmentMapper;
    private final OpenimFeignClient openimFeignClient;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public GroupInfo createGroup(GroupCreateDTO groupCreateDTO) {
        Long initiator = groupCreateDTO.getInitiator();//发起人id
        Long accepter = groupCreateDTO.getAccepter();//接受人id
        //群主为机器人
        //查询数据库师傅已经存在群组了
        OpenimGroup openimGroup = openimGroupMapper.groupIsExist(groupCreateDTO);
        if (openimGroup != null){
            return GroupInfo.builder()
                    .groupID(openimGroup.getOpenimGroupId())
                    .build();
        }
        //查询用户是否存在
        if (!BaseContext.getUserId().equals(initiator)){
            //判断发起人id和请求人相同
            throw new RoleErrorException("角色错误");
        }
        User user = userMapper.getById(initiator);
        User user1 = userMapper.getById(accepter);
        if (user == null || user1 == null) {
            throw new UserNotFoundException("用户不存在");
        }
        //检查招募存在，用户是否有对应的招募，
        Recruitment recruitment = recruitmentMapper.selectRecruitmentById(groupCreateDTO.getRecruitmentId());

        if (recruitment == null) {
            throw new RecruitmentNotFoundException("招募不存在");
        }
        if (!Objects.equals(recruitment.getUserId(), initiator) && !Objects.equals(recruitment.getUserId(), accepter)) {
            throw new RecruitmentNotFoundException("招募对应用户错误");
        }
        //群ID为拼接发起人ID_招募id_接受人id
        String groupID = initiator+"_"+groupCreateDTO.getRecruitmentId()+"_"+accepter;
        OpenimGroupCreateDTO.GroupInfo groupInfo = OpenimGroupCreateDTO.GroupInfo.builder()
                .groupID(groupID)
                .groupName(groupID)
                .groupType(2)
                .needVerification(1)
                .applyMemberFriend(1)
                .lookMemberInfo(0)
                .build();
        List<String> memberUserIDs = new ArrayList<>();
        //私聊
        if (Objects.equals(groupCreateDTO.getGroupType(), GroupTypeConstant.PRIVATE_CHAT)){
            //设置拓展字段，设置成员id
            groupInfo.setEx("私聊");
            memberUserIDs.add(initiator.toString());
            memberUserIDs.add(accepter.toString());
        }
        //客服
        if (Objects.equals(groupCreateDTO.getGroupType(), GroupTypeConstant.CUSTOMER_SERVICE)){
            //设置拓展字段，设置成员id
            groupInfo.setEx("客服");
            memberUserIDs.add(initiator.toString());
            memberUserIDs.add(accepter.toString());
            //TODO 需要完成客服分配
            //memberUserIDs.add()
        }
        OpenimGroupCreateDTO openimGroupCreateDTO = OpenimGroupCreateDTO.builder()
                .memberUserIDs(memberUserIDs)
                .ownerUserID("")//TODO 需要创建机器人账号，这里填写机器人账号
                .groupInfo(groupInfo)
                .build();
        //发送创建群组请求
        OpenimResult<GroupInfo> result = openimFeignClient.createGroup(
                String.valueOf(System.currentTimeMillis()),
                openImService.getAdminToken()
                ,openimGroupCreateDTO);
        if (result.getErrCode() != 0){
            throw new OpenimRequestErrorException("创建群组失败");
        }
        OpenimGroup openimGroup2 = OpenimGroup.builder()
                .recruitmentId(groupCreateDTO.getRecruitmentId())
                .employeeId(initiator)
                .employerId(accepter)
                .openimGroupId(groupID)
                .status(1)//默认群组状态为活跃
                .groupType(groupCreateDTO.getGroupType())//群组种类
                .build();
        //获取数据存入数据库
        openimGroupMapper.insertGroup(openimGroup2);
        //返回前端
        return result.getData();
        //前端需要将群两个人的名字头像信息存入数据库，
        //后端创建私聊需要完整的权限校验和权限限制
        //然后监听onGroupMemberInfoChanged回调，当发生改变的时候修改前端数据库，然后刷新界面信息
    }
}
