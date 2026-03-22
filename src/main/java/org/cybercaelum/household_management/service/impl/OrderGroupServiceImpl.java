package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.service.OpenImService;
import org.cybercaelum.household_management.service.OrderGroupService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 订单群组服务实现
 * @author CyberCaelum
 * @version 1.0
 * @date 2026/3/22
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderGroupServiceImpl implements OrderGroupService {

    private final OpenImService openImService;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String ORDER_GROUP_KEY_PREFIX = "order:group:";
    private static final long GROUP_EXPIRE_DAYS = 30; // 群组信息保留30天

    /**
     * @description 创建订单群组
     * @author CyberCaelum
     * @date 2026/3/22
     * @param orderId 订单ID
     * @param employerId 雇主ID
     * @param employeeId 雇员ID
     * @return java.lang.String 群组ID
     **/
    @Override
    public String createOrderGroup(Long orderId, Long employerId, Long employeeId) {
        String groupId = "order_" + orderId;
        String groupName = "订单#" + orderId + "服务群";
        
        List<String> members = new ArrayList<>();
        members.add(String.valueOf(employerId));
        if (employeeId != null) {
            members.add(String.valueOf(employeeId));
        }
        // 系统管理员作为群主
        String adminUserId = "imAdmin"; // 从配置读取
        
        try {
            String createdGroupId = openImService.createGroup(groupId, groupName, adminUserId, members);
            
            // 保存到Redis
            String key = ORDER_GROUP_KEY_PREFIX + orderId;
            stringRedisTemplate.opsForValue().set(key, createdGroupId, GROUP_EXPIRE_DAYS, TimeUnit.DAYS);
            
            // 发送欢迎消息
            openImService.sendGroupSystemMessage(createdGroupId, 
                    "订单服务群已创建，雇主和客服已加入。接单后雇员将自动加入。");
            
            log.info("订单群组创建成功: orderId={}, groupId={}", orderId, createdGroupId);
            return createdGroupId;
        } catch (Exception e) {
            log.error("订单群组创建失败: orderId={}", orderId, e);
            throw new RuntimeException("创建订单群组失败", e);
        }
    }

    /**
     * @description 雇员接单后加入群组
     * @author CyberCaelum
     * @date 2026/3/22
     * @param orderId 订单ID
     * @param employeeId 雇员ID
     **/
    @Override
    public void addEmployeeToGroup(Long orderId, Long employeeId) {
        String groupId = getOrderGroupId(orderId);
        if (groupId == null) {
            log.warn("订单群组不存在，无法添加雇员: orderId={}", orderId);
            return;
        }
        
        try {
            List<String> members = new ArrayList<>();
            members.add(String.valueOf(employeeId));
            openImService.inviteToGroup(groupId, members);
            
            openImService.sendGroupSystemMessage(groupId, 
                    "雇员已接单并加入群聊，可以开始服务沟通。");
            
            log.info("雇员加入订单群组: orderId={}, employeeId={}", orderId, employeeId);
        } catch (Exception e) {
            log.error("雇员加入订单群组失败: orderId={}, employeeId={}", orderId, employeeId, e);
        }
    }

    /**
     * @description 客服介入（加入群组）
     * @author CyberCaelum
     * @date 2026/3/22
     * @param orderId 订单ID
     * @param csUserId 客服用户ID
     **/
    @Override
    public void addCsToGroup(Long orderId, Long csUserId) {
        String groupId = getOrderGroupId(orderId);
        if (groupId == null) {
            log.warn("订单群组不存在，无法添加客服: orderId={}", orderId);
            return;
        }
        
        try {
            List<String> members = new ArrayList<>();
            members.add(String.valueOf(csUserId));
            openImService.inviteToGroup(groupId, members);
            
            openImService.sendGroupSystemMessage(groupId, 
                    "平台客服已介入，将协助处理当前问题。");
            
            log.info("客服加入订单群组: orderId={}, csUserId={}", orderId, csUserId);
        } catch (Exception e) {
            log.error("客服加入订单群组失败: orderId={}, csUserId={}", orderId, csUserId, e);
        }
    }

    /**
     * @description 获取订单群组ID
     * @author CyberCaelum
     * @date 2026/3/22
     * @param orderId 订单ID
     * @return java.lang.String 群组ID
     **/
    @Override
    public String getOrderGroupId(Long orderId) {
        String key = ORDER_GROUP_KEY_PREFIX + orderId;
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * @description 解散订单群组
     * @author CyberCaelum
     * @date 2026/3/22
     * @param orderId 订单ID
     **/
    @Override
    public void disbandOrderGroup(Long orderId) {
        String groupId = getOrderGroupId(orderId);
        if (groupId == null) {
            return;
        }
        
        // 发送结束消息
        try {
            openImService.sendGroupSystemMessage(groupId, "订单已结束，本群将在30天后解散。");
        } catch (Exception e) {
            log.warn("发送群组结束消息失败", e);
        }
        
        // 删除Redis记录
        String key = ORDER_GROUP_KEY_PREFIX + orderId;
        stringRedisTemplate.delete(key);
        
        log.info("订单群组已标记解散: orderId={}", orderId);
    }
}
