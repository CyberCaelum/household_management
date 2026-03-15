package org.cybercaelum.household_management.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.exception.OpenImException;
import org.cybercaelum.household_management.properties.OpenImProperties;
import org.cybercaelum.household_management.service.OpenImService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * OpenIM 服务类
 * @author CyberCaelum
 * @version 1.0
 * @description: 封装 OpenIM 的 API 调用
 * @date 2026/3/1 下午3:05
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OpenImServiceImpl implements OpenImService {

    private final OpenImProperties openImProperties;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 管理员 token 缓存
    private String adminToken;
    private long adminTokenExpireTime;

    /**
     * @description 获取管理员token
     * @author CyberCaelum
     * @date 2026/3/15
     * @return java.lang.String 管理员token
     **/
    public synchronized String getAdminToken() {
        // 如果 token 未过期，直接返回缓存的 token
        if (adminToken != null && System.currentTimeMillis() < adminTokenExpireTime) {
            return adminToken;
        }

        String url = openImProperties.getApiAddress() + "/auth/get_admin_token";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("operationID", String.valueOf(System.currentTimeMillis()));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("secret", openImProperties.getSecret());
        requestBody.put("userID", openImProperties.getAdminUserId());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            
            if (jsonNode.get("errCode").asInt() == 0) {
                adminToken = jsonNode.get("data").get("token").asText();
                long expireTimeSeconds = jsonNode.get("data").get("expireTimeSeconds").asLong();
                // 提前 10 分钟过期，避免边界问题
                adminTokenExpireTime = System.currentTimeMillis() + (expireTimeSeconds - 600) * 1000;
                return adminToken;
            } else {
                log.error("获取 OpenIM 管理员 token 失败: {}", jsonNode.get("errMsg").asText());
                throw new OpenImException("获取 OpenIM 管理员 token 失败: " + jsonNode.get("errMsg").asText());
            }
        } catch (Exception e) {
            log.error("获取 OpenIM 管理员 token 异常", e);
            throw new OpenImException("获取 OpenIM 管理员 token 异常"+e.getMessage());
        }
    }

    /**
     * @description 注册用户到OpenIm
     * @author CyberCaelum
     * @date 2026/3/15
     * @param userId 用户ID
     * @param nickname 用户昵称
     * @param faceUrl 用户头像URL
     **/
    public void registerUser(String userId, String nickname, String faceUrl) {
        String url = openImProperties.getApiAddress() + "/user/user_register";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("operationID", String.valueOf(System.currentTimeMillis()));
        headers.set("token", getAdminToken());

        List<Map<String, Object>> users = new ArrayList<>();
        Map<String, Object> user = new HashMap<>();
        user.put("userID", userId);
        user.put("nickname", nickname);
        user.put("faceURL", faceUrl != null ? faceUrl : "");
        users.add(user);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("users", users);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            
            if (jsonNode.get("errCode").asInt() != 0) {
                log.error("OpenIM 注册用户失败: {}", jsonNode.get("errMsg").asText());
                throw new OpenImException("OpenIM 注册用户失败: " + jsonNode.get("errMsg").asText());
            }
            log.info("OpenIM 注册用户成功: userId={}", userId);
        } catch (Exception e) {
            log.error("OpenIM 注册用户异常", e);
            throw new OpenImException("OpenIM 注册用户异常"+e.getMessage());
        }
    }

    /**
     * @description 获取用户token
     * @author CyberCaelum
     * @date 2026/3/15
     * @param userId 用户ID
     * @return java.lang.String 用户token
     **/
    public String getUserToken(String userId) {
        String url = openImProperties.getApiAddress() + "/auth/get_user_token";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("operationID", String.valueOf(System.currentTimeMillis()));
        headers.set("token", getAdminToken());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("platformID", openImProperties.getDefaultPlatformId());
        requestBody.put("userID", userId);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            
            if (jsonNode.get("errCode").asInt() == 0) {
                return jsonNode.get("data").get("token").asText();
            } else {
                log.error("获取 OpenIM 用户 token 失败: {}", jsonNode.get("errMsg").asText());
                throw new RuntimeException("获取 OpenIM 用户 token 失败: " + jsonNode.get("errMsg").asText());
            }
        } catch (Exception e) {
            log.error("获取 OpenIM 用户 token 异常", e);
            throw new RuntimeException("获取 OpenIM 用户 token 异常", e);
        }
    }

    /**
     * @description 更新用户信息
     * @author CyberCaelum
     * @date 2026/3/15
     * @param userId 用户ID
     * @param nickname 用户昵称（可选）
     * @param faceUrl  用户头像URL（可选）
     **/
    public void updateUserInfo(String userId, String nickname, String faceUrl) {
        String url = openImProperties.getApiAddress() + "/user/update_user_info_ex";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("operationID", String.valueOf(System.currentTimeMillis()));
        headers.set("token", getAdminToken());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userID", userId);
        if (nickname != null) {
            userInfo.put("nickname", nickname);
        }
        if (faceUrl != null) {
            userInfo.put("faceURL", faceUrl);
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userInfo", userInfo);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            
            if (jsonNode.get("errCode").asInt() != 0) {
                log.error("OpenIM 更新用户信息失败: {}", jsonNode.get("errMsg").asText());
                throw new RuntimeException("OpenIM 更新用户信息失败: " + jsonNode.get("errMsg").asText());
            }
            log.info("OpenIM 更新用户信息成功: userId={}", userId);
        } catch (Exception e) {
            log.error("OpenIM 更新用户信息异常", e);
            throw new RuntimeException("OpenIM 更新用户信息异常", e);
        }
    }
}
