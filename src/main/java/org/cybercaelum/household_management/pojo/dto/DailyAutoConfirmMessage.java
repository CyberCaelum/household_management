package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 每日服务自动确认消息
 * @date 2026/3/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyAutoConfirmMessage {
    private Long confirmationId;//确认记录id
    private Long orderId;//订单id
    private LocalDate serviceDate;//服务日期
    private Long employeeId;//家政人员id
    private LocalDateTime workerConfirmTime;//家政人员确认时间
    private LocalDateTime createTime;//消息创建时间
}
