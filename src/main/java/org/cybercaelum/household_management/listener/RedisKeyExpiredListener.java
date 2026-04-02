package org.cybercaelum.household_management.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.constant.CustomerServiceRedisKeyConstant;
import org.cybercaelum.household_management.service.CustomerServiceService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: redis过期监听
 * @date 2026/4/2 上午9:32
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RedisKeyExpiredListener implements MessageListener {

    private final StringRedisTemplate stringRedisTemplate;
    private final CustomerServiceService customerServiceService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 过期的键名
        String csSessionKey = message.toString();
        log.info("Received Redis Key: {}", csSessionKey);
        //分割获取userId
        String prefix = "cs:session:";
        try {
            if (csSessionKey.startsWith(prefix)) {
                String userId = csSessionKey.substring(prefix.length());
                //获取userId对应的csId
                String csUserSessionKey = CustomerServiceRedisKeyConstant.getCsUserSessionKey(Long.valueOf(userId));

                String csId = stringRedisTemplate.opsForValue().get(csUserSessionKey);
                //释放客服容量
                customerServiceService.releaseCsSession(Long.valueOf(csId),Long.valueOf(userId));
                // 执行后续操作
            }
        } catch (Exception e){
            log.error(e.getMessage());
        }
    }
}