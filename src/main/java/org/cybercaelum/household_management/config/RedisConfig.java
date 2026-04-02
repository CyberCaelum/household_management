package org.cybercaelum.household_management.config;

import org.cybercaelum.household_management.listener.RedisKeyExpiredListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: redis配置类
 * @date 2026/4/2 上午9:30
 */
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.database}")
    private int database;

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // 订阅所有数据库的过期事件，如果指定库号可以写 __keyevent@0__:expired
        container.addMessageListener(listenerAdapter, new PatternTopic("__keyevent@"+database+"__:expired"));
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(RedisKeyExpiredListener listener) {
        // 默认使用 onMessage 方法，也可指定其他方法名
        return new MessageListenerAdapter(listener, "onMessage");
    }
}
