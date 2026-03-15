package org.cybercaelum.household_management.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 每日服务确认
 * @date 2026/3/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyConfirmation {
    private Long id; // 主键
    private Long orderId; // 订单id
    private LocalDate serviceDate; // 服务日期
    private int status; // 确认状态：0待确认，1雇主已确认，2雇主拒绝/争议，3系统自动确认
    private LocalDateTime workerConfirmTime; // 家政人员发起确认的时间
    private LocalDateTime employerConfirmTime; // 雇主确认的时间
    private LocalDateTime autoConfirmTime; // 系统自动确认的时间
    private String disputeReason; // 争议原因
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
