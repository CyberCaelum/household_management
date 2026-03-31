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
import java.util.concurrent.TimeUnit;

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
    private static final long TASK_TTL_SECONDS = 20;

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
        //TODO 需要一个set维护所有客服的有效群聊，从群聊中查询是否存在这个群聊然后更新群聊状态，这个set使用用户id来表示
        //获取群组对应的redis中的key
        String csSessionKey = CustomerServiceRedisKeyConstant.getCsSessionKey(groupId);
        //刷新key对应的会话信息

        return OpenimCallbackDTO.builder().build();
    }
    //客服无需新建群组，只需在结束后将客服踢出，需要时加入客服，争议同理
    //创建群组并分配客服
    public String createCsGroup(Long userId){
        //TODO 需要实现分配算法
        String csId = "";
        //groupId = userId_csId_时间标识
        //redis中的groupId使用用户id方便查询
        String groupId = String.valueOf(userId);
        String csSessionKey = CustomerServiceRedisKeyConstant.getCsSessionKey(groupId);
        //查询是否存在用户对应的会话
        if (stringRedisTemplate.hasKey(csSessionKey)){
            //TODO 如果存在直接返回
            return "cs_" + userId;
        }
        Map<String,String> csInfo = new HashMap<>();
        csInfo.put("csId",String.valueOf(csId));
        csInfo.put("userId",String.valueOf(userId));
        csInfo.put("createTime",String.valueOf(System.currentTimeMillis()));
        csInfo.put("lastActiveTime",String.valueOf(System.currentTimeMillis()));
        csInfo.put("status",String.valueOf(1));
        //在redis中存储会话信息
        stringRedisTemplate.opsForHash().putAll(csSessionKey, csInfo);
        //增加对应的客服的状态信息，会话数量加1
        String onlineKey = CustomerServiceRedisKeyConstant.getCsOnlineKey(Long.valueOf(csId));
        stringRedisTemplate.opsForHash().increment(onlineKey, "currentSessions", 1);
        //设置会话过期时间
        stringRedisTemplate.expire(csSessionKey,20, TimeUnit.MINUTES);
        //返回拼接的会话
        return "";
    }
    //TODO 需要实现openim的消息回调，刷新群组的活跃时间

    //TODO 查看用户是否重复请求客服
    //TODO 排队机制
    //TODO 分配客服算法

}
