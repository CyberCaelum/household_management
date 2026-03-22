package org.cybercaelum.household_management.service;

/**
 * 订单群组服务
 * 管理每个订单对应的群组（雇员、雇主、客服）
 * @author CyberCaelum
 * @version 1.0
 * @date 2026/3/22
 */
public interface OrderGroupService {

    /**
     * 创建订单群组（下单后调用）
     * @param orderId 订单ID
     * @param employerId 雇主ID
     * @param employeeId 雇员ID（可为空，接单后再加入）
     * @return 群组ID
     */
    String createOrderGroup(Long orderId, Long employerId, Long employeeId);

    /**
     * 雇员接单后加入群组
     * @param orderId 订单ID
     * @param employeeId 雇员ID
     */
    void addEmployeeToGroup(Long orderId, Long employeeId);

    /**
     * 客服介入（加入群组）
     * @param orderId 订单ID
     * @param csUserId 客服用户ID
     */
    void addCsToGroup(Long orderId, Long csUserId);

    /**
     * 获取订单群组ID
     * @param orderId 订单ID
     * @return 群组ID（不存在返回null）
     */
    String getOrderGroupId(Long orderId);

    /**
     * 解散订单群组（订单结束后调用）
     * @param orderId 订单ID
     */
    void disbandOrderGroup(Long orderId);
}
