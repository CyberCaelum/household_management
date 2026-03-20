package org.cybercaelum.household_management.consumer;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.cybercaelum.household_management.constant.RocketMQConstant;
import org.cybercaelum.household_management.pojo.dto.PayTimeoutMessage;
import org.cybercaelum.household_management.service.OrderService;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 支付超时消费者（回调保底）
 * 处理支付回调超时后主动查询支付状态
 * @date 2026/3/20
 */
@Component
@RequiredArgsConstructor
@Slf4j
@RocketMQMessageListener(
        consumerGroup = "pay-timeout",
        topic = RocketMQConstant.PAY_TIMEOUT_TOPIC,
        tag = RocketMQConstant.PAY_TIMEOUT_TAG
)
public class PayTimeoutConsumer implements RocketMQListener {

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
            PayTimeoutMessage payMsg = JSON.parseObject(jsonStr, PayTimeoutMessage.class);
            log.info("收到支付超时消息，订单ID: {}，订单号: {}", 
                    payMsg.getOrderId(), payMsg.getOrderNumber());
            
            //使用服务层处理支付超时，主动查询支付状态
            orderService.handlePayTimeout(payMsg.getOrderId(), payMsg.getOrderNumber());
            
            return ConsumeResult.SUCCESS;
        } catch (Exception e) {
            log.error("处理支付超时消息失败: {}", jsonStr, e);
            // 消费失败，稍后重试
            return ConsumeResult.FAILURE;
        }
    }
}
