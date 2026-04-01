package org.cybercaelum.household_management.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.constant.CustomerServiceConstant;
import org.cybercaelum.household_management.constant.CustomerServiceRedisKeyConstant;
import org.cybercaelum.household_management.constant.OpenimCallbackCommandConstant;
import org.cybercaelum.household_management.constant.RoleConstant;
import org.cybercaelum.household_management.mapper.UserMapper;
import org.cybercaelum.household_management.pojo.dto.CsGroupAssignmentResult;
import org.cybercaelum.household_management.pojo.dto.MessageCallbackDTO;
import org.cybercaelum.household_management.pojo.dto.OpenimUserCallbackDTO;
import org.cybercaelum.household_management.pojo.dto.SessionEndDTO;
import org.cybercaelum.household_management.pojo.dto.WaitingUserDTO;
import org.cybercaelum.household_management.pojo.entity.User;
import org.cybercaelum.household_management.pojo.dto.OpenimCallbackDTO;
import org.cybercaelum.household_management.service.CustomerServiceService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final ObjectMapper objectMapper;
    private static final long TASK_TTL_MINUTES = 20;

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

        // 客服上线后，尝试处理等待队列中的用户
        processWaitingQueue();

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
        //用户不存在或不是客服直接放行
        if (user == null || RoleConstant.CUSTOMER_SERVICE != user.getRole()){
            return OpenimCallbackDTO.builder().build();
        }
        
        // 1. 先从在线集合中移除客服，防止 processWaitingQueue 将其作为候选
        stringRedisTemplate.opsForSet().remove(
                CustomerServiceRedisKeyConstant.CS_ONLINE_ALL_KEY,
                String.valueOf(csId));
        
        // 2. 结束该客服的所有会话（此时 processWaitingQueue 不会分配到这个已离线的客服）
        endAllSessionsByCs(csId);
        
        // 3. 删除在线状态哈希
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
            return OpenimCallbackDTO.error("发送群消息后的回调错误","发送群消息后的回调命令错误",400);
        }
        //获取聊天群组id
        String groupId = messageCallbackDTO.getGroupID();
        //客服群组的id是cs开头的，
        if (groupId.startsWith("cs_")){
            //取出其中的用户id,cs_userId
            String userIdStr = groupId.substring(3);
            //获取群组对应的redis中的key
            String csSessionKey = CustomerServiceRedisKeyConstant.getCsSessionKey(userIdStr);
            
            // 检查会话是否存在
            if (!stringRedisTemplate.hasKey(csSessionKey)) {
                // 会话不存在，需要重新匹配或加入排队
                Long userId = Long.valueOf(userIdStr);
                CsGroupAssignmentResult result = createCsGroup(userId);
                
                if (result.getStatus() == CsGroupAssignmentResult.Status.NO_AVAILABLE_CS) {
                    // 没有可用客服，加入等待队列
                    log.info("用户 {} 加入等待队列", userId);
                }
            } else {
                // 会话存在，刷新会话信息
                stringRedisTemplate.opsForHash().put(csSessionKey, "lastActiveTime", String.valueOf(System.currentTimeMillis()));
                //刷新过期时间
                stringRedisTemplate.expire(csSessionKey, TASK_TTL_MINUTES, TimeUnit.MINUTES);
            }
        }
        else {
            //不是客服群组直接放行
            return OpenimCallbackDTO.builder().build();
        }
        return OpenimCallbackDTO.builder().build();
    }
    //客服无需新建群组，只需在结束后将客服踢出，需要时加入客服，争议同理

    /**
     * @description 分配客服并记录redis客服信息，并加入群组
     * @author CyberCaelum
     * @date 2026/3/31
     * @param userId 用户id
     * @return CsGroupAssignmentResult
     **/
    public CsGroupAssignmentResult createCsGroup(Long userId){
        //查找最佳客服
        Long csIdLong = findBestCustomerService();
        if (csIdLong == null) {
            return CsGroupAssignmentResult.builder()
                    .status(CsGroupAssignmentResult.Status.NO_AVAILABLE_CS)
                    .csId("")
                    .message("当前无可用客服，已进入排队")
                    .build();
        }
        String csId = String.valueOf(csIdLong);
        //redis中的groupId使用用户id方便查询
        String groupId = String.valueOf(userId);
        String csSessionKey = CustomerServiceRedisKeyConstant.getCsSessionKey(groupId);
        //查询是redis否存在用户对应的会话，如果存在说明有客服处理，直接返回
        if (stringRedisTemplate.hasKey(csSessionKey)){
            //刷新key对应的会话信息
            stringRedisTemplate.opsForHash().put(csSessionKey, "lastActiveTime", String.valueOf(System.currentTimeMillis()));
            //刷新过期时间
            stringRedisTemplate.expire(csSessionKey, TASK_TTL_MINUTES, TimeUnit.MINUTES);
            return CsGroupAssignmentResult.builder()
                    .status(CsGroupAssignmentResult.Status.SESSION_EXISTS)
                    .csId(String.valueOf(stringRedisTemplate.opsForHash().get(csSessionKey,"csId")))
                    .message("用户已有会话")
                    .build();
        }
        Map<String,String> csInfo = new HashMap<>();
        csInfo.put("csId", String.valueOf(csId));
        csInfo.put("userId", String.valueOf(userId));
        csInfo.put("createTime", String.valueOf(System.currentTimeMillis()));
        csInfo.put("lastActiveTime", String.valueOf(System.currentTimeMillis()));
        csInfo.put("status", String.valueOf(1));
        //在redis中存储会话信息
        stringRedisTemplate.opsForHash().putAll(csSessionKey, csInfo);
        //增加对应的客服的状态信息，会话数量加1
        String onlineKey = CustomerServiceRedisKeyConstant.getCsOnlineKey(Long.valueOf(csId));
        String currentSessionsStr = (String) stringRedisTemplate.opsForHash().get(onlineKey, "currentSessions");
        int currentSessions = currentSessionsStr != null ? Integer.parseInt(currentSessionsStr) : 0;
        stringRedisTemplate.opsForHash().put(onlineKey, "currentSessions", String.valueOf(currentSessions + 1));
        //将会话信息添加到客服的会话集合中
        String csSessionsKey = CustomerServiceRedisKeyConstant.getCsSessionsKey(Long.valueOf(csId));
        stringRedisTemplate.opsForHash().put(csSessionsKey, groupId, String.valueOf(userId));
        //设置会话过期时间
        stringRedisTemplate.expire(csSessionKey, TASK_TTL_MINUTES, TimeUnit.MINUTES);

        //返回客服id
        return CsGroupAssignmentResult.builder()
                .status(CsGroupAssignmentResult.Status.SUCCESS)
                .csId(csId)
                .message("客服分配成功")
                .build();
    }

    /**
     * @description 查找最佳客服
     * @author CyberCaelum
     * @date 2026/3/31
     * @return java.lang.Long
     **/
    private Long findBestCustomerService(){
        Set<String> onlineCsIds = stringRedisTemplate.opsForSet().members(
                CustomerServiceRedisKeyConstant.CS_ONLINE_ALL_KEY
        );
        if (CollectionUtils.isEmpty(onlineCsIds)){
            return null;
        }
        Long bestCsId = null;
        int minLoad = Integer.MAX_VALUE;
        int maxSessions = Integer.MAX_VALUE;
        //实现最小负载优先
        for (String csIdStr : onlineCsIds){

            Long csId = Long.valueOf(csIdStr);
            String onlineKey = CustomerServiceRedisKeyConstant.getCsOnlineKey(csId);
            //获取客服在线信息
            Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(onlineKey);

            if (CollectionUtils.isEmpty(entries)) {
                continue;
            }
            int currentSessions = Integer.parseInt((String) entries.getOrDefault("currentSessions", "0"));
            int csMaxSessions = Integer.parseInt((String) entries.getOrDefault("maxSessions", "10"));
            // 检查是否还有空闲
            if (currentSessions >= csMaxSessions) {
                continue;
            }
            //防止除零异常
            if (csMaxSessions <= 0) continue;
            // 选择负载最小的客服（按负载率）
            double loadRate = (double) currentSessions / csMaxSessions;
            double minLoadRate = (double) minLoad / maxSessions;

            if (loadRate < minLoadRate) {
                minLoad = currentSessions;
                maxSessions = csMaxSessions;
                bestCsId = csId;
            }
        }
        return bestCsId;
    }

    /**
     * @description 将用户添加到等待队列（使用Set进行快速判重）
     * @author CyberCaelum
     * @date 2026/3/31
     * @param userId 用户id
     **/
    @Override
    public void addToWaitingQueue(Long userId) {
        // 使用Set进行快速判重，add返回的是添加到集合的元素数量（0或1）
        Long added = stringRedisTemplate.opsForSet().add(
                CustomerServiceRedisKeyConstant.CS_WAITING_SET_KEY,
                String.valueOf(userId)
        );
        
        // 如果返回0，说明用户已在队列中
        if (added == null || added == 0) {
            log.info("用户 {} 已在等待队列中，跳过重复添加", userId);
            return;
        }
        
        WaitingUserDTO waitingUser = WaitingUserDTO.builder()
                .userId(userId)
                .requestTime(System.currentTimeMillis())
                .build();
        
        try {
            String userJson = objectMapper.writeValueAsString(waitingUser);
            // 添加到队列尾部（FIFO）
            stringRedisTemplate.opsForList().rightPush(
                    CustomerServiceRedisKeyConstant.CS_WAITING_QUEUE_KEY, 
                    userJson
            );
        } catch (JsonProcessingException e) {
            log.error("添加用户到等待队列失败，userId={}", userId, e);
            // 添加失败时，从Set中移除
            stringRedisTemplate.opsForSet().remove(
                    CustomerServiceRedisKeyConstant.CS_WAITING_SET_KEY,
                    String.valueOf(userId)
            );
        }
    }

    /**
     * @description 检查用户是否已在等待队列中（使用Set快速判断）
     * @author CyberCaelum
     * @date 2026/3/31
     * @param userId 用户id
     * @return boolean
     **/
    private boolean isUserInWaitingQueue(Long userId) {
        return Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(
                CustomerServiceRedisKeyConstant.CS_WAITING_SET_KEY,
                String.valueOf(userId)
        ));
    }

    /**
     * @description 从等待队列中移除用户（同时从Set中移除）
     * @author CyberCaelum
     * @date 2026/3/31
     * @param userId 用户id
     **/
    private void removeFromWaitingQueue(Long userId) {
        // 先从Set中移除（快速操作）
        Long removed = stringRedisTemplate.opsForSet().remove(
                CustomerServiceRedisKeyConstant.CS_WAITING_SET_KEY,
                String.valueOf(userId)
        );
        
        // 如果Set中不存在，说明不在队列中
        if (removed == null || removed == 0) {
            return;
        }
        
        // 从List中移除
        List<String> queue = stringRedisTemplate.opsForList().range(
                CustomerServiceRedisKeyConstant.CS_WAITING_QUEUE_KEY, 
                0, 
                -1
        );
        if (CollectionUtils.isEmpty(queue)) {
            return;
        }
        
        for (String userJson : queue) {
            try {
                WaitingUserDTO waitingUser = objectMapper.readValue(userJson, WaitingUserDTO.class);
                if (userId.equals(waitingUser.getUserId())) {
                    stringRedisTemplate.opsForList().remove(
                            CustomerServiceRedisKeyConstant.CS_WAITING_QUEUE_KEY, 
                            1, 
                            userJson
                    );
                    break;
                }
            } catch (JsonProcessingException e) {
                log.error("解析等待队列用户信息失败", e);
            }
        }
    }

    /**
     * @description 处理等待队列 - 为等待中的用户分配客服
     * @author CyberCaelum
     * @date 2026/3/31
     **/
    private void processWaitingQueue() {
        while (true) {
            // 从队列左侧取出一个用户
            String userJson = stringRedisTemplate.opsForList().leftPop(
                    CustomerServiceRedisKeyConstant.CS_WAITING_QUEUE_KEY
            );
            
            if (userJson == null) {
                // 队列为空，结束处理
                break;
            }
            
            try {
                WaitingUserDTO waitingUser = objectMapper.readValue(userJson, WaitingUserDTO.class);
                Long userId = waitingUser.getUserId();
                
                // 从Set中移除（表示正在处理）
                stringRedisTemplate.opsForSet().remove(
                        CustomerServiceRedisKeyConstant.CS_WAITING_SET_KEY,
                        String.valueOf(userId)
                );
                
                // 尝试为该用户分配客服
                CsGroupAssignmentResult result = createCsGroup(userId);
                
                if (result.getStatus() == CsGroupAssignmentResult.Status.SUCCESS) {
                    log.info("为等待用户 {} 成功分配客服 {}", userId, result.getCsId());
                    // 可以继续处理下一个
                } else if (result.getStatus() == CsGroupAssignmentResult.Status.NO_AVAILABLE_CS) {
                    // 没有可用客服了，将用户放回队列头部和Set中
                    stringRedisTemplate.opsForList().leftPush(
                            CustomerServiceRedisKeyConstant.CS_WAITING_QUEUE_KEY, 
                            userJson
                    );
                    stringRedisTemplate.opsForSet().add(
                            CustomerServiceRedisKeyConstant.CS_WAITING_SET_KEY,
                            String.valueOf(userId)
                    );
                    log.info("客服已满，用户 {} 继续等待", userId);
                    break;
                }
                // 如果用户已有会话，不需要特殊处理，直接处理下一个（因为已经从Set中移除了）
            } catch (JsonProcessingException e) {
                log.error("解析等待队列用户信息失败", e);
            }
        }
    }

    /**
     * @description 获取用户在等待队列中的位置
     * @author CyberCaelum
     * @date 2026/3/31
     * @param userId 用户id
     * @return 位置（从0开始），如果不在队列中返回-1
     **/
    public int getWaitingPosition(Long userId) {
        // 先使用Set快速判断是否在队列中
        if (!isUserInWaitingQueue(userId)) {
            return -1;
        }
        
        List<String> queue = stringRedisTemplate.opsForList().range(
                CustomerServiceRedisKeyConstant.CS_WAITING_QUEUE_KEY, 
                0, 
                -1
        );
        if (CollectionUtils.isEmpty(queue)) {
            return -1;
        }
        
        for (int i = 0; i < queue.size(); i++) {
            try {
                WaitingUserDTO waitingUser = objectMapper.readValue(queue.get(i), WaitingUserDTO.class);
                if (userId.equals(waitingUser.getUserId())) {
                    return i;
                }
            } catch (JsonProcessingException e) {
                log.error("解析等待队列用户信息失败", e);
            }
        }
        return -1;
    }

    /**
     * @description 结束会话，可能是客户主动结束，客服主动结束，超时自动结束
     * @author CyberCaelum
     * @date 2026/3/31
     * @param csId 客服id
     * @param userId 用户id
     **/
    private void releaseCsSession(Long csId, Long userId){
        String onlineKey = CustomerServiceRedisKeyConstant.getCsOnlineKey(csId);
        // 使用读取-计算-写入保证类型一致
        String currentSessionsStr = (String) stringRedisTemplate.opsForHash().get(onlineKey, "currentSessions");
        int currentSessions = currentSessionsStr != null ? Integer.parseInt(currentSessionsStr) : 0;
        if (currentSessions > 0) {
            stringRedisTemplate.opsForHash().put(onlineKey, "currentSessions", String.valueOf(currentSessions - 1));
        }
        
        String csSessionKey = CustomerServiceRedisKeyConstant.getCsSessionKey(String.valueOf(userId));
        stringRedisTemplate.delete(csSessionKey);
        
        // 从客服的会话集合中移除
        String csSessionsKey = CustomerServiceRedisKeyConstant.getCsSessionsKey(csId);
        stringRedisTemplate.opsForHash().delete(csSessionsKey, String.valueOf(userId));
        
        // 会话结束后，尝试处理等待队列中的用户
        processWaitingQueue();
    }

    /**
     * @description 主动结束会话
     * @author CyberCaelum
     * @date 2026/3/31
     * @param sessionEndDTO 结束会话信息
     * @return 是否成功
     **/
    @Override
    public boolean endSession(SessionEndDTO sessionEndDTO) {
        Long userId = sessionEndDTO.getUserId();
        Long csId = sessionEndDTO.getCsId();
        String csSessionKey = CustomerServiceRedisKeyConstant.getCsSessionKey(String.valueOf(userId));
        
        // 检查会话是否存在
        if (!stringRedisTemplate.hasKey(csSessionKey)) {
            log.warn("结束会话失败，会话不存在：userId={}", userId);
            return false;
        }
        
        // 如果提供了客服ID，直接使用；否则从会话中获取
        if (csId == null) {
            Object csIdObj = stringRedisTemplate.opsForHash().get(csSessionKey, "csId");
            if (csIdObj == null) {
                log.warn("结束会话失败，无法获取客服ID：userId={}", userId);
                return false;
            }
            csId = Long.valueOf(csIdObj.toString());
        }
        
        // 记录结束原因
        log.info("结束会话：userId={}, csId={}, reason={}", userId, csId, sessionEndDTO.getReason());
        
        // 释放会话
        releaseCsSession(csId, userId);
        return true;
    }
    
    /**
     * @description 获取用户的当前会话信息
     * @author CyberCaelum
     * @date 2026/3/31
     * @param userId 用户id
     * @return 会话信息map，如果没有会话返回null
     **/
    @Override
    public Map<String, String> getUserSession(Long userId) {
        String csSessionKey = CustomerServiceRedisKeyConstant.getCsSessionKey(String.valueOf(userId));
        
        if (!stringRedisTemplate.hasKey(csSessionKey)) {
            return null;
        }
        
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(csSessionKey);
        if (CollectionUtils.isEmpty(entries)) {
            return null;
        }
        
        Map<String, String> sessionInfo = new HashMap<>();
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            sessionInfo.put(entry.getKey().toString(), entry.getValue() != null ? entry.getValue().toString() : null);
        }
        return sessionInfo;
    }
    
    /**
     * @description 处理会话超时
     * @author CyberCaelum
     * @date 2026/3/31
     * @param userId 用户id
     **/
    @Override
    public void handleSessionTimeout(Long userId) {
        Map<String, String> session = getUserSession(userId);
        if (session != null) {
            String csId = session.get("csId");
            log.info("会话超时：userId={}, csId={}", userId, csId);
            
            SessionEndDTO sessionEndDTO = SessionEndDTO.builder()
                    .userId(userId)
                    .csId(csId != null ? Long.valueOf(csId) : null)
                    .reason(SessionEndDTO.REASON_TIMEOUT)
                    .build();
            endSession(sessionEndDTO);
        }
    }
    
    /**
     * @description 客服离线时结束其所有会话
     * @author CyberCaelum
     * @date 2026/3/31
     * @param csId 客服id
     **/
    @Override
    public void endAllSessionsByCs(Long csId) {
        log.info("客服离线，结束其所有会话：csId={}", csId);
        
        // 遍历所有会话，找到属于该客服的会话
        // 由于Redis不支持直接搜索hash中的字段，我们需要使用客服会话集合
        String csSessionsKey = CustomerServiceRedisKeyConstant.getCsSessionsKey(csId);
        Map<Object, Object> sessions = stringRedisTemplate.opsForHash().entries(csSessionsKey);
        
        if (!CollectionUtils.isEmpty(sessions)) {
            for (Map.Entry<Object, Object> entry : sessions.entrySet()) {
                String userId = entry.getValue().toString();
                SessionEndDTO sessionEndDTO = SessionEndDTO.builder()
                        .userId(Long.valueOf(userId))
                        .csId(csId)
                        .reason(SessionEndDTO.REASON_CS_OFFLINE)
                        .build();
                endSession(sessionEndDTO);
            }
        }
        
        // 删除客服会话集合
        stringRedisTemplate.delete(csSessionsKey);
    }
}
