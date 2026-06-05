package org.cybercaelum.household_management.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.constant.CustomerServiceRedisKeyConstant;
import org.cybercaelum.household_management.exception.OpenimRequestErrorException;
import org.cybercaelum.household_management.feign.OpenimFeignClient;
import org.cybercaelum.household_management.pojo.dto.GetUsersOnlineStatusDTO;
import org.cybercaelum.household_management.pojo.dto.UserOnlineStatusDTO;
import org.cybercaelum.household_management.pojo.entity.OpenimResult;
import org.cybercaelum.household_management.service.CustomerServiceService;
import org.cybercaelum.household_management.service.OpenImService;
import org.cybercaelum.household_management.service.UserService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
   private final CustomerServiceService customerServiceService;

   /**
    * 用于异步延迟任务的调度器（代替 Thread.sleep，避免阻塞定时任务线程）
    */
   private static final ScheduledExecutorService SCHEDULER =
           Executors.newSingleThreadScheduledExecutor(r -> {
               Thread t = new Thread(r, "cs-offline-cleaner");
               t.setDaemon(true);
               return t;
           });

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
           return;
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

       if (offlineUserIds.isEmpty()) {
           return;
       }

       log.info("检测到疑似离线客服: {}，等待3秒后二次确认", offlineUserIds);

       // 延迟3秒后异步二次确认，避免OpenIM状态延迟导致的误判
       // 使用ScheduledExecutorService代替Thread.sleep，避免阻塞定时任务线程
       final Set<String> suspectedOfflineIds = new HashSet<>(offlineUserIds);
       SCHEDULER.schedule(() -> {
           try {
               // 二次确认：重新查询这些疑似离线的客服状态
               GetUsersOnlineStatusDTO confirmDTO = new GetUsersOnlineStatusDTO(
                       new ArrayList<>(suspectedOfflineIds));
               OpenimResult<List<UserOnlineStatusDTO>> confirmResult = openimFeignClient.getUsersOnlineStatus(
                       String.valueOf(System.currentTimeMillis()),
                       openImService.getAdminToken(),
                       confirmDTO);

               if (confirmResult.getErrCode() != 0) {
                   log.error("二次确认用户在线状态失败: {}", confirmResult.getErrMsg());
                   return;
               }

               // 最终确认离线的客服
               Set<String> confirmedOfflineIds = new HashSet<>();
               for (UserOnlineStatusDTO userStatus : confirmResult.getData()) {
                   if (userStatus.getStatus() != 1) {
                       confirmedOfflineIds.add(userStatus.getUserID());
                   }
               }

               if (confirmedOfflineIds.isEmpty()) {
                   log.info("二次确认后无离线客服，跳过清理");
                   return;
               }

               log.info("二次确认后离线客服: {}，执行清理", confirmedOfflineIds);

               //遍历离线客服清除会话信息
               for (String userId : confirmedOfflineIds) {
                   //将客服信息从set中删除
                   stringRedisTemplate.opsForSet().remove(
                           CustomerServiceRedisKeyConstant.CS_ONLINE_ALL_KEY,
                           userId);
                   //删除在线状态
                   String onlineKey = CustomerServiceRedisKeyConstant.getCsOnlineKey(Long.valueOf(userId));
                   //结束客服的会话
                   customerServiceService.endAllSessionsByCs(Long.valueOf(userId));
                   stringRedisTemplate.delete(onlineKey);
               }
           } catch (Exception e) {
               log.error("异步清理离线客服失败", e);
           }
       }, 3, TimeUnit.SECONDS);
   }
}
