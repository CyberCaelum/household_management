package org.cybercaelum.household_management.constant;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 客服redis的key
 * @date 2026/3/29
 */
public class CustomerServiceRedisKeyConstant {

    /**
     * 客服在线状态 Hash
     * cs:online:{csId} -> {loginTime, maxSessions, currentSessions, lastHeartbeat}
     */
    public static final String CS_ONLINE_KEY = "cs:online:%s";

    /**
     * 所有在线客服集合 Set
     * cs:online:all -> [csId1, csId2, ...]
     */
    public static final String CS_ONLINE_ALL_KEY = "cs:online:all";

    /**
     * 客服会话集合 Hash
     * cs:sessions:{csId} -> {sessionId: userId}
     */
    public static final String CS_SESSIONS_KEY = "cs:sessions:%s";

    /**
     * 会话详情 Hash
     * cs:session:{userId} -> {csId, userId, createTime, lastActivityTime, status}
     */
    public static final String CS_SESSION_KEY = "cs:session:%s";

    /**
     * 用户当前会话 String
     * cs:user:{userId} -> csId
     */
    public static final String CS_USER_SESSION_KEY = "cs:user:%s";

    /**
     * 等待分配的用户队列 List
     * cs:waiting:queue -> [{userId, requestTime}]
     */
    public static final String CS_WAITING_QUEUE_KEY = "cs:waiting:queue";
    
    /**
     * 等待队列用户ID集合 Set（用于快速判重）
     * cs:waiting:set -> [userId1, userId2, ...]
     */
    public static final String CS_WAITING_SET_KEY = "cs:waiting:set";

    /**
     * 客服统计信息 Hash
     * cs:stats:{csId}:{date} -> {totalSessions, totalDuration, avgResponseTime}
     */
    public static final String CS_STATS_KEY = "cs:stats:%s:%s";

    /**
     * 会话锁（用于并发控制）
     * cs:lock:session:{sessionId}
     */
    public static final String CS_SESSION_LOCK_KEY = "cs:lock:session:%s";

    /**
     * 客服锁（用于分配时并发控制）
     * cs:lock:cs:{csId}
     */
    public static final String CS_LOCK_KEY = "cs:lock:cs:%s";

    /**
     * 获取客服在线状态Key
     */
    public static String getCsOnlineKey(Long csId) {
        return String.format(CS_ONLINE_KEY, csId);
    }

    /**
     * 获取客服会话集合Key
     */
    public static String getCsSessionsKey(Long csId) {
        return String.format(CS_SESSIONS_KEY, csId);
    }

    /**
     * 获取会话详情Key
     */
    public static String getCsSessionKey(String userId) {
        return String.format(CS_SESSION_KEY, userId);
    }

    /**
     * 获取用户当前会话Key
     */
    public static String getCsUserSessionKey(Long userId) {
        return String.format(CS_USER_SESSION_KEY, userId);
    }

    /**
     * 获取客服统计信息Key
     */
    public static String getCsStatsKey(Long csId, String date) {
        return String.format(CS_STATS_KEY, csId, date);
    }

    /**
     * 获取会话锁Key
     */
    public static String getCsSessionLockKey(String sessionId) {
        return String.format(CS_SESSION_LOCK_KEY, sessionId);
    }

    /**
     * 获取客服锁Key
     */
    public static String getCsLockKey(Long csId) {
        return String.format(CS_LOCK_KEY, csId);
    }
}
