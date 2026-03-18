package org.cybercaelum.household_management.consumer;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.cybercaelum.household_management.constant.RocketMQConstant;
import org.cybercaelum.household_management.pojo.dto.OrderTimeoutMessage;
import org.cybercaelum.household_management.service.OrderService;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 消费者
 * @date 2026/3/17
 */
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        consumerGroup = "order-cancel",
        topic = RocketMQConstant.ORDER_TIMEOUT_TOPIC,
        tag = RocketMQConstant.ORDER_CANCEL_TAG
)
public class OrderConsumer implements RocketMQListener {

    private final OrderService orderService;

    @Override
    public ConsumeResult consume(MessageView messageView) {
        //提取ByteBuffer
        ByteBuffer bufferBytes = messageView.getBody();
        byte[] body = new byte[bufferBytes.remaining()];
        bufferBytes.get(body);
        String jsonStr = new String(body, StandardCharsets.UTF_8);
        //转为类对象
        OrderTimeoutMessage orderMsg = JSON.parseObject(jsonStr, OrderTimeoutMessage.class);
        //使用服务层处理超时订单，设置订单状态为取消
        orderService.orderTimeOut(orderMsg.getId());
        return ConsumeResult.SUCCESS;
    }
}