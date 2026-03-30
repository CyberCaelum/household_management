package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.constant.CustomerServiceConstant;
import org.cybercaelum.household_management.constant.CustomerServiceRedisKeyConstant;
import org.cybercaelum.household_management.constant.OpenimCallbackCommandConstant;
import org.cybercaelum.household_management.constant.RoleConstant;
import org.cybercaelum.household_management.mapper.UserMapper;
import org.cybercaelum.household_management.pojo.dto.OpenimUserCallbackDTO;
import org.cybercaelum.household_management.pojo.entity.User;
import org.cybercaelum.household_management.pojo.vo.OpenimCallbackVO;
import org.cybercaelum.household_management.service.OpenimUserCallBackService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: openim用户相关回调服务类
 * @date 2026/3/28
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OpenimUserCallBackServiceImpl implements OpenimUserCallBackService {

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
    public OpenimCallbackVO afterOnline(OpenimUserCallbackDTO userCallbackDTO) {
        //验证回调命令是否正确
        if (!OpenimCallbackCommandConstant.AFTER_USER_ONLINE_COMMAND.equals(userCallbackDTO.getCallbackCommand())){
            return OpenimCallbackVO.error("用户在线状态回调错误","用户在线状态回调命令错误",400);
        }
        //获取登录用户信息
        Long csId = Long.valueOf(userCallbackDTO.getUserID());
        User user = userMapper.getById(csId);
        if (user == null) {//用户不存在
            return OpenimCallbackVO.error("用户在线状态回调错误","服务器用户不存在",400);
        }
        //如果登录不是客服放行
        if (RoleConstant.CUSTOMER_SERVICE != user.getRole()){
            return OpenimCallbackVO.builder().build();
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

        return OpenimCallbackVO.builder().build();
    }

    /**
     * @description 用户离线状态回调，处理客服离线
     * @author CyberCaelum
     * @date 2026/3/29
     * @param userCallbackDTO 回调信息
     * @return org.cybercaelum.household_management.pojo.vo.OpenimCallbackVO
     **/
    @Override
    public OpenimCallbackVO afterOffLine(OpenimUserCallbackDTO userCallbackDTO) {
        //验证回调命令是否正确
        if (!OpenimCallbackCommandConstant.AFTER_USER_OFFLINE_COMMAND.equals(userCallbackDTO.getCallbackCommand())){
            return OpenimCallbackVO.builder()
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
            return OpenimCallbackVO.builder().build();
        }
        //将客服信息从set中删除
        stringRedisTemplate.opsForSet().remove(
                CustomerServiceRedisKeyConstant.CS_ONLINE_ALL_KEY,
                String.valueOf(csId));
        //删除在线状态
        String onlineKey = CustomerServiceRedisKeyConstant.getCsOnlineKey(csId);
        stringRedisTemplate.delete(onlineKey);
        return OpenimCallbackVO.builder().build();
    }
}
