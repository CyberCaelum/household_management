package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.service.CustomerServiceNotificationService;
import org.cybercaelum.household_management.service.OpenImService;
import org.cybercaelum.household_management.service.OrderGroupService;
import org.springframework.stereotype.Service;

/**
 * 客服通知服务实现
 * @author CyberCaelum
 * @version 1.0
 * @date 2026/3/22
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceNotificationServiceImpl implements CustomerServiceNotificationService {

    private final OpenImService openImService;
    private final OrderGroupService orderGroupService;

    /**
     * @description 通知每日服务争议
     * @author CyberCaelum
     * @date 2026/3/22
     * @param orderId 订单ID
     * @param confirmationId 确认记录ID
     * @param serviceDate 服务日期
     * @param reason 争议原因
     **/
    @Override
    public void notifyDailyServiceDispute(Long orderId, Long confirmationId, String serviceDate, String reason) {
        String groupId = orderGroupService.getOrderGroupId(orderId);
        if (groupId == null) {
            log.warn("订单群组不存在，无法发送争议通知: orderId={}", orderId);
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append("📢 【服务争议提醒】\n");
        content.append("服务日期：").append(serviceDate).append("\n");
        content.append("争议原因：").append(reason).append("\n");
        content.append("--------------------------------\n");
        content.append("平台客服已收到争议申请，将尽快介入处理。\n");
        content.append("处理期间请勿重复提交。");

        sendNotification(groupId, content.toString(), "服务争议");
    }

    /**
     * @description 通知取消申请超时（转平台介入）
     * @author CyberCaelum
     * @date 2026/3/22
     * @param orderId 订单ID
     * @param applicationId 申请ID
     * @param cancelType 取消类型
     **/
    @Override
    public void notifyCancelApplicationTimeout(Long orderId, Long applicationId, Integer cancelType) {
        String groupId = orderGroupService.getOrderGroupId(orderId);
        if (groupId == null) {
            log.warn("订单群组不存在，无法发送超时通知: orderId={}", orderId);
            return;
        }

        String typeName = getCancelTypeName(cancelType);

        StringBuilder content = new StringBuilder();
        content.append("📢 【取消申请超时】\n");
        content.append("申请类型：").append(typeName).append("\n");
        content.append("--------------------------------\n");
        content.append("对方在规定时间内未响应取消申请，\n");
        content.append("平台客服将介入处理，请耐心等待。");

        sendNotification(groupId, content.toString(), "取消申请超时");
    }

    /**
     * @description 通知退款异常需要人工处理
     * @author CyberCaelum
     * @date 2026/3/22
     * @param orderId 订单ID
     * @param refundNo 退款单号
     * @param errorMsg 错误信息
     **/
    @Override
    public void notifyRefundException(Long orderId, String refundNo, String errorMsg) {
        String groupId = orderGroupService.getOrderGroupId(orderId);
        if (groupId == null) {
            log.warn("订单群组不存在，无法发送退款异常通知: orderId={}", orderId);
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append("🚨 【退款异常提醒】\n");
        content.append("退款单号：").append(refundNo).append("\n");
        content.append("--------------------------------\n");
        content.append("退款处理出现异常，平台客服将人工介入处理。\n");
        content.append("请耐心等待，处理结果将在此群通知。");

        sendNotification(groupId, content.toString(), "退款异常");

        // 同时给管理员发单聊通知（高优先级）
        notifyAdmin("退款异常需处理", "订单ID: " + orderId + ", 退款单号: " + refundNo + ", 错误: " + errorMsg);
    }

    /**
     * @description 通知订单异常（通用）
     * @author CyberCaelum
     * @date 2026/3/22
     * @param orderId 订单ID
     * @param exceptionType 异常类型
     * @param message 异常信息
     **/
    @Override
    public void notifyOrderException(Long orderId, String exceptionType, String message) {
        String groupId = orderGroupService.getOrderGroupId(orderId);
        if (groupId == null) {
            log.warn("订单群组不存在，无法发送异常通知: orderId={}", orderId);
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append("🚨 【订单异常】\n");
        content.append("异常类型：").append(exceptionType).append("\n");
        content.append("--------------------------------\n");
        content.append(message).append("\n");
        content.append("平台客服将介入处理，请耐心等待。");

        sendNotification(groupId, content.toString(), "订单异常");
    }

    /**
     * @description 通知订单支付成功
     * @author CyberCaelum
     * @date 2026/3/22
     * @param orderId 订单ID
     * @param amount 支付金额
     **/
    @Override
    public void notifyOrderPaid(Long orderId, String amount) {
        String groupId = orderGroupService.getOrderGroupId(orderId);
        if (groupId == null) {
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append("✅ 【订单已支付】\n");
        content.append("支付金额：¥").append(amount).append("\n");
        content.append("--------------------------------\n");
        content.append("雇主已完成支付，等待雇员接单。");

        sendNotification(groupId, content.toString(), "订单支付");
    }

    /**
     * @description 通知订单退款成功
     * @author CyberCaelum
     * @date 2026/3/22
     * @param orderId 订单ID
     * @param refundAmount 退款金额
     * @param reason 退款原因
     **/
    @Override
    public void notifyOrderRefunded(Long orderId, String refundAmount, String reason) {
        String groupId = orderGroupService.getOrderGroupId(orderId);
        if (groupId == null) {
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append("💰 【退款已到账】\n");
        content.append("退款金额：¥").append(refundAmount).append("\n");
        content.append("退款原因：").append(reason).append("\n");
        content.append("--------------------------------\n");
        content.append("退款已原路返回，请注意查收。");

        sendNotification(groupId, content.toString(), "退款成功");
    }

    /**
     * @description 发送通知到群组
     * @author CyberCaelum
     * @date 2026/3/22
     * @param groupId 群组ID
     * @param content 消息内容
     * @param notificationType 通知类型（用于日志）
     **/
    private void sendNotification(String groupId, String content, String notificationType) {
        try {
            openImService.sendGroupTextMessage(groupId, content);
            log.info("客服通知已发送: type={}, groupId={}", notificationType, groupId);
        } catch (Exception e) {
            log.error("客服通知发送失败: type={}, groupId={}", notificationType, groupId, e);
        }
    }

    /**
     * @description 通知管理员（用于高优先级问题）
     * @author CyberCaelum
     * @date 2026/3/22
     * @param title 标题
     * @param message 消息内容
     **/
    private void notifyAdmin(String title, String message) {
        try {
            // 可以配置多个管理员ID
            String adminId = "admin"; // 从配置读取
            openImService.sendSystemNotification(adminId, title + "\n" + message);
        } catch (Exception e) {
            log.error("管理员通知发送失败", e);
        }
    }

    /**
     * @description 获取取消类型名称
     * @author CyberCaelum
     * @date 2026/3/22
     * @param cancelType 取消类型
     * @return java.lang.String 类型名称
     **/
    private String getCancelTypeName(Integer cancelType) {
        if (cancelType == null) {
            return "未知";
        }
        switch (cancelType) {
            case 1: return "协商一致取消";
            case 2: return "雇主强制取消";
            case 3: return "家政人员强制取消";
            default: return "其他";
        }
    }
}
