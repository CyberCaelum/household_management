package org.cybercaelum.household_management.consumer;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.cybercaelum.household_management.constant.RocketMQConstant;
import org.cybercaelum.household_management.pojo.dto.RefundTimeoutMessage;
import org.cybercaelum.household_management.service.OrderService;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 退款超时消费者
 * 处理退款超时后主动查询退款状态
 * @date 2026/3/20
 */
@Component
@RequiredArgsConstructor
@Slf4j
@RocketMQMessageListener(
        consumerGroup = "refund-timeout",
        topic = RocketMQConstant.REFUND_TIMEOUT_TOPIC,
        tag = RocketMQConstant.REFUND_TIMEOUT_TAG
)
public class RefundTimeoutConsumer implements RocketMQListener {

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
            RefundTimeoutMessage refundMsg = JSON.parseObject(jsonStr, RefundTimeoutMessage.class);
            log.info("收到退款超时消息，订单ID: {}，退款单号: {}", 
                    refundMsg.getOrderId(), refundMsg.getRefundNumber());
            
            //使用服务层处理退款超时，主动查询退款状态
            orderService.handleRefundTimeout(refundMsg.getOrderId(), refundMsg.getRefundNumber());
            
            return ConsumeResult.SUCCESS;
        } catch (Exception e) {
            log.error("处理退款超时消息失败: {}", jsonStr, e);
            // 消费失败，稍后重试
            return ConsumeResult.FAILURE;
        }
    }
}
