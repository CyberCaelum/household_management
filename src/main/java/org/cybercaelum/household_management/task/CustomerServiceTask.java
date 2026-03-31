package org.cybercaelum.household_management.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.constant.CustomerServiceRedisKeyConstant;
import org.cybercaelum.household_management.service.CustomerServiceService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 客服定时任务
 * @date 2026/3/31
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceTask {

    private final StringRedisTemplate stringRedisTemplate;
    private final CustomerServiceService customerServiceService;

    /**
     * @description 定时检查等待队列，处理超时等待的用户
     * @author CyberCaelum
     * @date 2026/3/31
     **/
    @Scheduled(fixedDelay = 30000) // 30秒检查一次
    public void processWaitingQueueTask() {
        // 实际上processWaitingQueue会在客服上线和会话结束时自动调用
        // 这里可以作为额外的保险机制
        log.debug("定时检查等待队列");
    }
    
    /**
     * @description 检查会话过期并清理
     * Redis的TTL会自动处理过期，这里主要用于记录日志和统计
     * @author CyberCaelum
     * @date 2026/3/31
     **/
    @Scheduled(fixedDelay = 60000) // 1分钟检查一次
    public void checkExpiredSessionsTask() {
        log.debug("检查过期会话");
        // Redis会自动过期，这里可以添加额外的业务逻辑
        // 比如统计、通知等
    }
}
