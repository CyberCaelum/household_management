package org.cybercaelum.household_management.service;

import java.util.List;

/**
 * OpenIM 服务接口
 * @author CyberCaelum
 * @version 1.0
 * @description: 封装 OpenIM 的 API 调用
 * @date 2026/3/1 下午3:05
 */
public interface OpenImService {

    /**
     * 获取管理员 token
     * @return 管理员 token
     */
    String getAdminToken();

    /**
     * 注册用户到 OpenIM
     * @param userId 用户ID
     * @param nickname 用户昵称
     * @param faceUrl 用户头像URL
     */
    void registerUser(String userId, String nickname, String faceUrl);

    /**
     * 获取用户 token
     * @param userId 用户ID
     * @return token 字符串
     */
    String getUserToken(String userId);

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param nickname 用户昵称（可选）
     * @param faceUrl 用户头像URL（可选）
     */
    void updateUserInfo(String userId, String nickname, String faceUrl);

    // ==================== 群组管理 ====================

    /**
     * 创建群组
     * @param groupId 群组ID（业务自定义）
     * @param groupName 群组名称
     * @param ownerUserId 群主用户ID
     * @param memberUserIds 成员用户ID列表
     * @return 群组ID
     */
    String createGroup(String groupId, String groupName, String ownerUserId, List<String> memberUserIds);

    /**
     * 邀请用户加入群组
     * @param groupId 群组ID
     * @param userIds 用户ID列表
     */
    void inviteToGroup(String groupId, List<String> userIds);

    /**
     * 发送系统消息到群组
     * @param groupId 群组ID
     * @param content 消息内容
     */
    void sendGroupSystemMessage(String groupId, String content);

    /**
     * 发送文本消息到群组（以管理员身份）
     * @param groupId 群组ID
     * @param content 消息内容
     */
    void sendGroupTextMessage(String groupId, String content);

    // ==================== 单聊消息 ====================

    /**
     * 发送系统通知给用户
     * @param userId 用户ID
     * @param content 通知内容
     */
    void sendSystemNotification(String userId, String content);
}
