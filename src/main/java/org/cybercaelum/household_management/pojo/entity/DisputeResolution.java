package org.cybercaelum.household_management.pojo.entity;

import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 争议处理基础类
 * @date 2026/3/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DisputeResolution {
    private Long id;//主键
    private Long orderId;//订单id
    private Integer sourceType;//争议来源，1-取消申请，2-每日确认，3-其他
    private Long sourceId;//来源记录id，cancel_application_id或daily_confirmation_id
    private Integer defaultingParty;//平台裁定的违约方：1-雇主，2-雇员
    private Integer decision;// 平台裁决结果：1-同意取消，2-拒绝取消，3-部分结算等
    private Long operatorId;//平台操作人
    private String note;//平台备注
    private LocalDateTime createdTime;//创建时间
}
