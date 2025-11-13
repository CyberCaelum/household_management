package org.cybercaelum.household_management.context;

import org.cybercaelum.household_management.constant.JwtClaimsConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 线程局部存储类
 * @date 2025/10/18 下午3:34
 */
public class BaseContext {
    private static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    /**
     * @description 新增线程存储内容
     * @author CyberCaelum
     * @date 下午3:45 2025/10/18
     * @param key key
     * @param value 内容
     **/
    public static void set(String key, Object value) {
        Map<String, Object> context = threadLocal.get();
        if (context == null) {
            context = new HashMap<>();
            threadLocal.set(context);
        }
        context.put(key, value);
    }
    /**
     * @description 通过key获得线程存储的内容
     * @author CyberCaelum
     * @date 下午3:46 2025/10/18
     * @param key key
     * @return java.lang.Object
     **/
    public static Object get(String key) {
        Map<String, Object> context = threadLocal.get();
        return context != null ? context.get(key) : null;
    }
    /**
     * @description 设置用户的id
     * @author CyberCaelum
     * @date 下午3:50 2025/10/18 
     * @param userId 用户id
     **/
    public static void setUserId(Long userId) {
        set(JwtClaimsConstant.USER_ID, userId);
    }
    /**
     * @description 获得用户id
     * @author CyberCaelum
     * @date 下午3:50 2025/10/18  
     * @return java.lang.Long
     **/
    public static Long getUserId() {
        return (Long) get(JwtClaimsConstant.USER_ID);
    }
    /**
     * @description 设置用户的角色
     * @author CyberCaelum
     * @date 下午3:50 2025/10/18 
     * @param role 用户角色
     **/
    public static void setRole(Integer role){
        set("role", role);
    }
    /**
     * @description 获得用户的角色
     * @author CyberCaelum
     * @date 下午3:51 2025/10/18  
     * @return java.lang.Integer
     **/
    public static Integer getRole(){
        return (Integer) get("role");
    }
    /**
     * @description 移除信息
     * @author CyberCaelum
     * @date 下午3:52 2025/10/18
     **/
    public static void remove() {
        threadLocal.remove();
    }
}
