package org.cybercaelum.household_management.consumer;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 消费者
 * @date 2026/3/17
 */
//@Component
//@RocketMQMessageListener(
//        topic = "ORDER_TIMEOUT_TOPIC",
//        consumerGroup = "ORDER_CANCEL_GROUP",
//        selectorExpression = "ORDER_CANCEL"  // Tag 过滤
//)
//public class OrderConsumer implements MessageListenerConcurrently {
//
//    @Override
//    public ConsumeConcurrentlyStatus consumeMessage(
//            List<MessageExt> msgs,
//            ConsumeConcurrentlyContext context) {
//
//        for (MessageExt msg : msgs) {
//            try {
//                String body = new String(msg.getBody(), java.nio.charset.StandardCharsets.UTF_8);
//                String tag = msg.getTags();
//
//                System.out.println("收到消息，tag: " + tag + ", body: " + body);
//
//                // 业务处理
//                // ...
//
//            } catch (Exception e) {
//                System.err.println("消费失败: " + e.getMessage());
//                // 重试
//                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
//            }
//        }
//
//        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//    }
//}