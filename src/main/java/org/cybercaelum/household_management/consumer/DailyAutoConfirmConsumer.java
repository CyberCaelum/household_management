package org.cybercaelum.household_management.consumer;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.cybercaelum.household_management.constant.RocketMQConstant;
import org.cybercaelum.household_management.pojo.dto.DailyAutoConfirmMessage;
import org.cybercaelum.household_management.service.OrderService;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 每日服务自动确认消费者
 * 家政人员确认24小时后，雇主未确认/未争议，自动确认
 * @date 2026/3/22
 */
@Component
@RequiredArgsConstructor
@Slf4j
@RocketMQMessageListener(
        consumerGroup = "daily-auto-confirm",
        topic = RocketMQConstant.DAILY_AUTO_CONFIRM_TOPIC,
        tag = RocketMQConstant.DAILY_AUTO_CONFIRM_TAG
)
public class DailyAutoConfirmConsumer implements RocketMQListener {

    private final OrderService orderService;

    @Override
    public ConsumeResult consume(MessageView messageView) {
        //提取ByteBuffer
        ByteBuffer bufferBytes = messageView.getBody();
        byte[] body = new byte[bufferBytes.remaining()];
        bufferBytes.get(body);
        String jsonStr = new String(body, StandardCharsets.UTF_8);

        try {
            //转为类对象
            DailyAutoConfirmMessage message = JSON.parseObject(jsonStr, DailyAutoConfirmMessage.class);
            log.info("收到每日服务自动确认消息，confirmationId: {}，orderId: {}，serviceDate: {}",
                    message.getConfirmationId(), message.getOrderId(), message.getServiceDate());

            //调用服务层处理自动确认
            orderService.handleDailyAutoConfirm(message.getConfirmationId());

            return ConsumeResult.SUCCESS;
        } catch (Exception e) {
            log.error("处理每日服务自动确认消息失败: {}", jsonStr, e);
            // 消费失败，稍后重试
            return ConsumeResult.FAILURE;
        }
    }
}
