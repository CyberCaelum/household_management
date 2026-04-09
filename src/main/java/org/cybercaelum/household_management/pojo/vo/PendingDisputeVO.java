package org.cybercaelum.household_management.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 待处理争议VO
 * @date 2026/4/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PendingDisputeVO {
    private Long id; // 争议ID
    private Integer disputeType; // 争议类型：1-取消申请争议，2-每日确认争议
    private String disputeTypeName; // 争议类型名称
    private Long orderId; // 订单ID
    private String orderNumber; // 订单编号
    private Long sourceId; // 来源记录ID（cancel_application_id 或 daily_confirmation_id）
    private String reason; // 争议原因/申请理由
    private Integer status; // 状态
    private String statusName; // 状态名称
    private Long applicantId; // 申请人ID
    private String applicantName; // 申请人姓名
    private Integer applicantRole; // 申请人角色：1-雇主，2-家政人员
    private String applicantRoleName; // 申请人角色名称
    private LocalDate serviceDate; // 服务日期（仅每日确认争议有）
    private LocalDateTime createTime; // 创建时间
}
