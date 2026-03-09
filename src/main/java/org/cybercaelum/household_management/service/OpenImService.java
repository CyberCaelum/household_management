package org.cybercaelum.household_management.service;

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
}
