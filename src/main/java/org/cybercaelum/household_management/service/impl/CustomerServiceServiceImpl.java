package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.constant.CustomerServiceConstant;
import org.cybercaelum.household_management.constant.CustomerServiceRedisKeyConstant;
import org.cybercaelum.household_management.constant.OpenimCallbackCommandConstant;
import org.cybercaelum.household_management.constant.RoleConstant;
import org.cybercaelum.household_management.mapper.UserMapper;
import org.cybercaelum.household_management.pojo.dto.MessageCallbackDTO;
import org.cybercaelum.household_management.pojo.dto.OpenimUserCallbackDTO;
import org.cybercaelum.household_management.pojo.entity.User;
import org.cybercaelum.household_management.pojo.dto.OpenimCallbackDTO;
import org.cybercaelum.household_management.service.CustomerServiceService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 客服服务类
 * @date 2026/3/30
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceServiceImpl implements CustomerServiceService {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String,Object> redisTemplate;
    private final UserMapper userMapper;

    /**
     * @description 用户登录状态回调，处理客服在线状态
     * @author CyberCaelum
     * @date 2026/3/29
     * @param userCallbackDTO 用户信息
     * @return org.cybercaelum.household_management.pojo.vo.OpenimCallbackVO
     **/
    @Override
    public OpenimCallbackDTO afterOnline(OpenimUserCallbackDTO userCallbackDTO) {
        //验证回调命令是否正确
        if (!OpenimCallbackCommandConstant.AFTER_USER_ONLINE_COMMAND.equals(userCallbackDTO.getCallbackCommand())){
            return OpenimCallbackDTO.error("用户在线状态回调错误","用户在线状态回调命令错误",400);
        }
        //获取登录用户信息
        Long csId = Long.valueOf(userCallbackDTO.getUserID());
        User user = userMapper.getById(csId);
        if (user == null) {//用户不存在
            return OpenimCallbackDTO.error("用户在线状态回调错误","服务器用户不存在",400);
        }
        //如果登录不是客服放行
        if (RoleConstant.CUSTOMER_SERVICE != user.getRole()){
            return OpenimCallbackDTO.builder().build();
        }
        //将客服的信息放入redis的所有在线客服合集
        stringRedisTemplate.opsForSet().add(
                CustomerServiceRedisKeyConstant.CS_ONLINE_ALL_KEY,
                String.valueOf(csId)
        );
        //存储客服在线状态
        String onlineKey = CustomerServiceRedisKeyConstant.getCsOnlineKey(csId);
        Map<String,String> csInfo = new HashMap<>();
        csInfo.put("csId",String.valueOf(csId));//客服id
        csInfo.put("maxSessions",String.valueOf(CustomerServiceConstant.DEFAULT_MAX_SESSIONS));//最大会话数量限制
        csInfo.put("currentSessions","0");//目前会话数量
        stringRedisTemplate.opsForHash().putAll(onlineKey, csInfo);

        return OpenimCallbackDTO.builder().build();
    }

    /**
     * @description 用户离线状态回调，处理客服离线
     * @author CyberCaelum
     * @date 2026/3/29
     * @param userCallbackDTO 回调信息
     * @return org.cybercaelum.household_management.pojo.vo.OpenimCallbackVO
     **/
    @Override
    public OpenimCallbackDTO afterOffLine(OpenimUserCallbackDTO userCallbackDTO) {
        //验证回调命令是否正确
        if (!OpenimCallbackCommandConstant.AFTER_USER_OFFLINE_COMMAND.equals(userCallbackDTO.getCallbackCommand())){
            return OpenimCallbackDTO.builder()
                    .actionCode(1)
                    .errCode(400)
                    .errMsg("用户离线状态回调错误")
                    .errDlt("用户离线状态回调命令错误")
                    .nextCode("1")
                    .build();
        }
        //获取离线用户信息
        Long csId = Long.valueOf(userCallbackDTO.getUserID());
        User user = userMapper.getById(csId);
        //如果离线不是客服放行
        if (RoleConstant.CUSTOMER_SERVICE != user.getRole()){
            return OpenimCallbackDTO.builder().build();
        }
        //将客服信息从set中删除
        stringRedisTemplate.opsForSet().remove(
                CustomerServiceRedisKeyConstant.CS_ONLINE_ALL_KEY,
                String.valueOf(csId));
        //删除在线状态
        String onlineKey = CustomerServiceRedisKeyConstant.getCsOnlineKey(csId);
        stringRedisTemplate.delete(onlineKey);
        return OpenimCallbackDTO.builder().build();
    }

    /**
     * @description 刷新客服聊天群组在线状态
     * @author CyberCaelum
     * @date 2026/3/30
     * @param messageCallbackDTO 回调信息
     **/
    @Override
    public OpenimCallbackDTO freshCsGroup(MessageCallbackDTO messageCallbackDTO) {
        //判断回调命令
        if (!OpenimCallbackCommandConstant.AFTER_SEND_GROUP_MSG_COMMAND.equals(messageCallbackDTO.getCallbackCommand())){
            return OpenimCallbackDTO.builder()
                    .actionCode(1)
                    .errCode(400)
                    .errMsg("发送群消息后的回调错误")
                    .errDlt("发送群消息后的回调命令错误")
                    .nextCode("1")
                    .build();
        }
        //获取聊天群组id
        String groupId = messageCallbackDTO.getGroupID();
        //TODO 需要一个set维护所有客服的有效群聊，从群聊中查询是否存在这个群聊然后更新群聊状态
        return OpenimCallbackDTO.builder().build();
    }
    //TODO 分配客服，需要用户id，需要存储群组信息，name:用户id，map："客服名"：kefuid,"创建时间"：时间，“最后活跃时间”：时间，“过期时间”：时间

    //TODO 需要实现openim的消息回调，刷新群组的活跃时间

    //TODO 查看用户是否重复请求客服
    //TODO 排队机制
    //TODO 分配客服算法

}
