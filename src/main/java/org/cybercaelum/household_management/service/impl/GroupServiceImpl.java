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
import org.cybercaelum.household_management.mapper.RecruitmentMapper;
import org.cybercaelum.household_management.mapper.UserMapper;
import org.cybercaelum.household_management.pojo.dto.GroupCreateDTO;
import org.cybercaelum.household_management.pojo.dto.ImportFriendDTO;
import org.cybercaelum.household_management.pojo.dto.OpenimGroupCreateDTO;
import org.cybercaelum.household_management.pojo.entity.OpenimGroup;
import org.cybercaelum.household_management.pojo.entity.OpenimResult;
import org.cybercaelum.household_management.pojo.entity.Recruitment;
import org.cybercaelum.household_management.pojo.entity.User;
import org.cybercaelum.household_management.pojo.vo.GroupInfo;
import org.cybercaelum.household_management.service.GroupService;
import org.cybercaelum.household_management.service.OpenImService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
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
    private final RecruitmentMapper recruitmentMapper;
    private final OpenimFeignClient openimFeignClient;
    private final UserMapper userMapper;
    //TODO 私聊
    //TODO 获取发起人和接收人，将群主设为机器人，将群id拼接，发起创建请求，创建失败获取失败错误码，解析错误码，
    //如果错误码是群已存在，查询群信息发送给前端，如果创建成功返回群信息

    //TODO 客服

    //TODO 争议
    @Transactional
    @Override
    public GroupInfo createGroup(GroupCreateDTO groupCreateDTO) {
        Long initiator = groupCreateDTO.getInitiator();//发起人id
        Long accepter = groupCreateDTO.getAccepter();//接受人id
        //群主为机器人

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

            //设置拓展字段，设置成员id
            groupInfo.setEx("私聊");
            memberUserIDs.add(initiator.toString());
            memberUserIDs.add(accepter.toString());

        //客服

            //设置拓展字段，设置成员id
            groupInfo.setEx("客服");
            memberUserIDs.add(initiator.toString());
            memberUserIDs.add(accepter.toString());
            //TODO 需要完成客服分配
            //memberUserIDs.add()

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
        //返回前端
        return result.getData();
        //前端需要将群两个人的名字头像信息存入数据库，
        //后端创建私聊需要完整的权限校验和权限限制
        //然后监听onGroupMemberInfoChanged回调，当发生改变的时候修改前端数据库，然后刷新界面信息
    }

    //创建私聊
    @Override
    public GroupInfo createPrivateChat(GroupCreateDTO groupCreateDTO) {

        Long initiatorId = groupCreateDTO.getInitiator();//发起人id
        Long accepterId = groupCreateDTO.getAccepter();//接受人id
        //群主为机器人

        //查询用户是否存在
        if (!BaseContext.getUserId().equals(initiatorId)){
            //判断发起人id和请求人相同
            throw new RoleErrorException("角色错误");
        }
        User initiator = userMapper.getById(initiatorId);
        User accepter = userMapper.getById(accepterId);
        if (initiator == null || accepter == null) {
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
        //TODO 先将连个用户加好友，方便前端获取好友信息更改的回调，更新群显示信息
        ImportFriendDTO importFriendDTO = ImportFriendDTO.builder()
                .ownerUserID(String.valueOf(initiatorId))
                .friendUserIDs(new ArrayList<>(Arrays.asList(String.valueOf(accepterId))))
                .build();
        //调用openim添加好友
        openimFeignClient.importFriend(
                String.valueOf(System.currentTimeMillis()),
                openImService.getAdminToken(),
                importFriendDTO
        );
        //TODO 判断是否添加成功，
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
        //设置拓展字段，设置成员id
        groupInfo.setEx("私聊");
        memberUserIDs.add(initiator.toString());
        memberUserIDs.add(accepter.toString());
        return null;
    }
}
