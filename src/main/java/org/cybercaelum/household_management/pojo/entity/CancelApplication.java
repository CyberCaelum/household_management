package org.cybercaelum.household_management.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 取消申请表
 * @date 2026/3/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CancelApplication {
    private Long id; // 主键
    private Long orderId; // 关联订单ID
    private Long applicantId; // 申请人ID
    private int applicantRole; // 申请人角色：1-雇主，2-家政人员
    private int cancelType; // 申请的取消类型：1-协商一致，2-雇主强制，3-家政人员强制
    private String reason; // 申请理由
    private int status; // 申请状态：1-待对方确认，2-对方已同意，3-对方已拒绝，4-平台介入处理中，5-平台已裁决
    private Long confirmUserId; // 确认方用户ID（当对方同意/拒绝时记录）
    private LocalDateTime confirmTime; // 确认时间
    private int platformDecision; // 平台裁决结果：1-同意取消，2-拒绝取消，3-部分结算等
    private Long platformOperator; // 平台操作人
    private String platformNote; // 平台备注
    private LocalDateTime expireTime; // 超时时间（如24小时内未确认则自动转平台介入）
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
