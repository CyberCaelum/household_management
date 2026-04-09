package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.constant.GroupTypeConstant;
import org.cybercaelum.household_management.constant.RobotConstant;
import org.cybercaelum.household_management.context.BaseContext;
import org.cybercaelum.household_management.exception.*;
import org.cybercaelum.household_management.feign.OpenimFeignClient;
import org.cybercaelum.household_management.mapper.DailyConfirmationMapper;
import org.cybercaelum.household_management.mapper.OrderMapper;
import org.cybercaelum.household_management.mapper.RecruitmentMapper;
import org.cybercaelum.household_management.mapper.UserMapper;
import org.cybercaelum.household_management.pojo.dto.*;
import org.cybercaelum.household_management.pojo.entity.*;
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
    private final OrderMapper orderMapper;
    private final DailyConfirmationMapper dailyConfirmationMapper;

    /**
     * @description 创建私聊
     * @author CyberCaelum
     * @date 2026/3/30
     * @param groupCreateDTO 群聊信息
     * @return org.cybercaelum.household_management.pojo.vo.GroupInfo
     **/
    @Transactional
    @Override
    public GroupInfo createPrivateChat(GroupCreateDTO groupCreateDTO) {

        Long initiatorId = groupCreateDTO.getInitiator();//发起人id
        Long accepterId = groupCreateDTO.getAccepter();//接受人id

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
        if (!Objects.equals(recruitment.getUserId(), initiatorId) && !Objects.equals(recruitment.getUserId(), accepterId)) {
            throw new RecruitmentNotFoundException("招募对应用户错误");
        }

        //先将用户加好友，方便前端获取好友信息更改的回调，更新群显示信息
        ImportFriendDTO importFriendDTO = ImportFriendDTO.builder()
                .ownerUserID(String.valueOf(initiatorId))
                .friendUserIDs(new ArrayList<>(Arrays.asList(String.valueOf(accepterId))))
                .build();
        //调用openim添加好友
        OpenimResult<Object> result = openimFeignClient.importFriend(
                String.valueOf(System.currentTimeMillis()),
                openImService.getAdminToken(),
                importFriendDTO
        );
        //判断是否添加成功
        if (result.getErrCode() != 1304 && result.getErrCode() != 0){
            throw new OpenimRequestErrorException("用户好友添加失败");
        }

        //设置群组信息，群ID为拼接发起人ID_招募id_接受人id
        String groupID = initiatorId+"_"+groupCreateDTO.getRecruitmentId()+"_"+accepterId;
        OpenimGroupCreateDTO.GroupInfo groupInfo = OpenimGroupCreateDTO.GroupInfo.builder()
                .groupID(groupID)
                .groupName(groupID)
                .build();
        List<String> memberUserIDs = new ArrayList<>();
        //设置拓展字段，设置成员id
        groupInfo.setEx("私聊");
        memberUserIDs.add(initiatorId.toString());
        memberUserIDs.add(accepterId.toString());
        //设置群组
        OpenimGroupCreateDTO openimGroupCreateDTO = OpenimGroupCreateDTO.builder()
                .memberUserIDs(memberUserIDs)
                .ownerUserID(RobotConstant.id.toString())
                .groupInfo(groupInfo)
                .build();
        //发送创建群组请求
        OpenimResult<GroupInfo> groupResult = openimFeignClient.createGroup(
                String.valueOf(System.currentTimeMillis()),
                openImService.getAdminToken()
                ,openimGroupCreateDTO);
        //判断是否错误或者已经存在群组
        if (groupResult.getErrCode() != 0 && groupResult.getErrCode() != 1202){
            throw new OpenimRequestErrorException("创建群组失败");
        }
        //群组已存在,查询群组信息并返回
        if (groupResult.getErrCode() == 1202){
            OpenimResult<GroupListDTO> groupsInfo = openimFeignClient.getGroupsInfo(
                    String.valueOf(System.currentTimeMillis()),
                    openImService.getAdminToken()
                    ,new ArrayList<>(Arrays.asList(groupID))
            );
            if (groupsInfo.getErrCode() != 0){
                throw new OpenimRequestErrorException("查询群组信息失败");
            }
            return groupsInfo.getData().getGroupInfos().get(0);
        }
        //返回前端
        return groupResult.getData();
    }

    /**
     * @description 创建客服群组
     * @author CyberCaelum
     * @date 2026/3/30
     * @param groupCreateDTO 群组信息
     * @return org.cybercaelum.household_management.pojo.vo.GroupInfo
     **/
    @Override
    public GroupInfo createCsChat(GroupCreateDTO groupCreateDTO) {
        Long initiatorId = groupCreateDTO.getInitiator();//发起人id
        if (groupCreateDTO.getAccepter()!=null){
            throw new GroupCreateErrorException("创建客服群组错误");
        }
        User initiator = userMapper.getById(initiatorId);
        if (initiator == null) {
            throw new UserNotFoundException("用户不存在");
        }

        //创建id为cs_userId的客服群聊
        String groupID = "cs_" + initiatorId;
        
        //设置群组信息
        OpenimGroupCreateDTO.GroupInfo groupInfo = OpenimGroupCreateDTO.GroupInfo.builder()
                .groupID(groupID)
                .groupName("客服咨询-" + initiatorId)
                .build();
        
        List<String> memberUserIDs = new ArrayList<>();
        //设置拓展字段
        groupInfo.setEx("客服");
        memberUserIDs.add(initiatorId.toString());
        //TODO 需要增加机器人

        //设置群组
        OpenimGroupCreateDTO openimGroupCreateDTO = OpenimGroupCreateDTO.builder()
                .memberUserIDs(memberUserIDs)
                .ownerUserID(RobotConstant.id.toString())
                .groupInfo(groupInfo)
                .build();
        //发送创建群组请求
        OpenimResult<GroupInfo> groupResult = openimFeignClient.createGroup(
                String.valueOf(System.currentTimeMillis()),
                openImService.getAdminToken()
                ,openimGroupCreateDTO);
        //判断是否错误或者已经存在群组
        if (groupResult.getErrCode() != 0 && groupResult.getErrCode() != 1202){
            throw new OpenimRequestErrorException("创建客服群组失败");
        }
        //群组已存在,查询群组信息并返回
        if (groupResult.getErrCode() == 1202){
            OpenimResult<GroupListDTO> groupsInfo = openimFeignClient.getGroupsInfo(
                    String.valueOf(System.currentTimeMillis()),
                    openImService.getAdminToken()
                    ,new ArrayList<>(Arrays.asList(groupID))
            );
            if (groupsInfo.getErrCode() != 0){
                throw new OpenimRequestErrorException("查询客服群组信息失败");
            }
            return groupsInfo.getData().getGroupInfos().get(0);
        }
        //返回前端
        return groupResult.getData();
    }

    //TODO 只创建一个争议群聊，不分类型，详细信息由系统号发送到群聊中进行标识
    /**
     * @description 创建争议群组
     * @author CyberCaelum
     * @date 上午9:56 2026/4/9
     * @param disputeSessionDTO 争议信息
     * @return org.cybercaelum.household_management.pojo.vo.GroupInfo
     **/
    @Override
    public GroupInfo createDisputeChat(DisputeSessionDTO disputeSessionDTO){
        Long employerId = 0L;
        Long employeeId = 0L;
        OpenimGroupCreateDTO.GroupInfo groupInfo = new OpenimGroupCreateDTO.GroupInfo();
        //订单争议
        if (disputeSessionDTO.getOrderId() != null
                && disputeSessionDTO.getDailyConfirmationId() == null){
            //查找订单信息，确认聊天双方
            Order order = orderMapper.getOrderById(disputeSessionDTO.getOrderId());
            employerId = order.getEmployerId();
            employeeId = order.getEmployeeId();
        }
        //每日确定争议
        if (disputeSessionDTO.getOrderId() !=null
                && disputeSessionDTO.getDailyConfirmationId() != null) {
            //查找订单确认聊天双方，
            DailyConfirmation dailyConfirmation = dailyConfirmationMapper.selectById(disputeSessionDTO.getDailyConfirmationId());
            Order order = orderMapper.getOrderById(dailyConfirmation.getOrderId());
            employerId = order.getEmployerId();
            employeeId = order.getEmployeeId();
        }
        else {
            throw new GroupCreateErrorException("争议群组创建失败");
        }
        //创建群组id
        String groupID = "dispute"+"_"+employeeId+"_"+employerId;
        //创建群组信息
        groupInfo.setGroupID(groupID);
        groupInfo.setGroupName("争议处理-" + disputeSessionDTO.getOrderId());
        groupInfo.setEx("争议处理");
        //设置群组成员
        List<String> memberUserIDs = new ArrayList<>();
        memberUserIDs.add(employeeId.toString());
        memberUserIDs.add(employerId.toString());
        //设置群组
        OpenimGroupCreateDTO openimGroupCreateDTO = OpenimGroupCreateDTO.builder()
                .memberUserIDs(memberUserIDs)
                .ownerUserID(RobotConstant.id.toString())
                .groupInfo(groupInfo)
                .build();
        //发送创建群组请求
        OpenimResult<GroupInfo> groupResult = openimFeignClient.createGroup(
                String.valueOf(System.currentTimeMillis()),
                openImService.getAdminToken()
                ,openimGroupCreateDTO);
        //判断是否错误或者已经存在群组
        if (groupResult.getErrCode() != 0 && groupResult.getErrCode() != 1202){
            throw new OpenimRequestErrorException("创建客服群组失败");
        }
        //群组已存在,查询群组信息并返回
        if (groupResult.getErrCode() == 1202){
            OpenimResult<GroupListDTO> groupsInfo = openimFeignClient.getGroupsInfo(
                    String.valueOf(System.currentTimeMillis()),
                    openImService.getAdminToken()
                    ,new ArrayList<>(Arrays.asList(groupID))
            );
            if (groupsInfo.getErrCode() != 0){
                throw new OpenimRequestErrorException("查询客服群组信息失败");
            }
            return groupsInfo.getData().getGroupInfos().get(0);
        }
        //返回前端
        return groupResult.getData();
    }
}
