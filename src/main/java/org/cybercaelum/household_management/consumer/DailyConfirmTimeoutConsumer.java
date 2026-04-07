package org.cybercaelum.household_management.consumer;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.cybercaelum.household_management.constant.RocketMQConstant;
import org.cybercaelum.household_management.pojo.dto.DailyConfirmTimeoutMessage;
import org.cybercaelum.household_management.service.OrderService;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 每日服务自动确认超时消费者
 * @date 2026/3/23
 */
@Component
@RequiredArgsConstructor
@Slf4j
@RocketMQMessageListener(
        endpoints = "127.0.0.1:8081",
        consumerGroup = "daily-confirm-timeout",
        topic = RocketMQConstant.DAILY_CONFIRM_TIMEOUT_TOPIC,
        tag = RocketMQConstant.DAILY_CONFIRM_TIMEOUT_TAG
)
public class DailyConfirmTimeoutConsumer implements RocketMQListener {

    private final OrderService orderService;

    @Override
    public ConsumeResult consume(MessageView messageView) {
        try {
            //提取ByteBuffer
            ByteBuffer bufferBytes = messageView.getBody();
            byte[] body = new byte[bufferBytes.remaining()];
            bufferBytes.get(body);
            String jsonStr = new String(body, StandardCharsets.UTF_8);
            //转为类对象
            DailyConfirmTimeoutMessage message = JSON.parseObject(jsonStr, DailyConfirmTimeoutMessage.class);
            log.info("收到每日服务自动确认超时消息，确认记录ID: {}", message.getConfirmationId());
            //使用服务层处理自动确认
            orderService.autoConfirmDailyServiceById(message.getConfirmationId());
            return ConsumeResult.SUCCESS;
        } catch (Exception e) {
            log.error("处理每日服务自动确认超时消息失败", e);
            return ConsumeResult.FAILURE;
        }
    }
}
