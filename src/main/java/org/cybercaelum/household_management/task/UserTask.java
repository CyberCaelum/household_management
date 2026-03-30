package org.cybercaelum.household_management.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.constant.CustomerServiceRedisKeyConstant;
import org.cybercaelum.household_management.exception.OpenimRequestErrorException;
import org.cybercaelum.household_management.feign.OpenimFeignClient;
import org.cybercaelum.household_management.pojo.dto.GetUsersOnlineStatusDTO;
import org.cybercaelum.household_management.pojo.dto.UserOnlineStatusDTO;
import org.cybercaelum.household_management.pojo.entity.OpenimResult;
import org.cybercaelum.household_management.service.OpenImService;
import org.cybercaelum.household_management.service.UserService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 用户定时任务
 * @date 2026/3/29
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UserTask {

    private final StringRedisTemplate stringRedisTemplate;
    private final OpenimFeignClient openimFeignClient;
    private final OpenImService openImService;

    /**
     * @description 定时清理不在线的客服
     * @author CyberCaelum
     * @date 2026/3/29
     **/
    @Scheduled(fixedDelay = 60000)//一分钟
    public void cleanOfflineCustomerServiceTask(){
        //从redis中获取在线的客服
        Set<String> onlineCsIds = stringRedisTemplate.opsForSet().members(
                CustomerServiceRedisKeyConstant.CS_ONLINE_ALL_KEY);
        if (onlineCsIds == null || onlineCsIds.isEmpty()) {
            return; // 没有在线客服，直接返回
        }
        //从openim中获取在线状态
        GetUsersOnlineStatusDTO getUsersOnlineStatusDTO = new GetUsersOnlineStatusDTO(new ArrayList<>(onlineCsIds));
        OpenimResult<List<UserOnlineStatusDTO>> userOnlineStatusDTOs = openimFeignClient.getUsersOnlineStatus(
                String.valueOf(System.currentTimeMillis()),
                openImService.getAdminToken(),
                getUsersOnlineStatusDTO);
        //判断请求是否成功
        if (userOnlineStatusDTOs.getErrCode() != 0){
            log.error("获取用户在线状态失败: {}", userOnlineStatusDTOs.getErrMsg());
        }
        //对比在线状态
        Set<String> trulyOnlineUserIds = new HashSet<>();
        for (UserOnlineStatusDTO userStatus : userOnlineStatusDTOs.getData()){
            if (userStatus.getStatus() == 1){
                trulyOnlineUserIds.add(userStatus.getUserID());
            }
        }
        //计算离线客服
        Set<String> offlineUserIds = new HashSet<>(onlineCsIds);
        offlineUserIds.removeAll(trulyOnlineUserIds);

        //遍历离线客服清除会话信息
        for (String userId : offlineUserIds){
            //将客服信息从set中删除
            stringRedisTemplate.opsForSet().remove(
                    CustomerServiceRedisKeyConstant.CS_ONLINE_ALL_KEY,
                    String.valueOf(userId));
            //删除在线状态
            String onlineKey = CustomerServiceRedisKeyConstant.getCsOnlineKey(Long.valueOf(userId));
            stringRedisTemplate.delete(onlineKey);
        }
    }
}
