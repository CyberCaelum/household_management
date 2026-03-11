package org.cybercaelum.household_management.pojo.dto;

import jakarta.annotation.security.DenyAll;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 用户下订单
 * @date 2026/3/9 上午10:27
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrdersSubmitDTO {
    private Long recruitmentId;//订单对应的招募id
    private BigDecimal price;//价格
    private LocalDate startTime;//订单开始时间
    private LocalDate endTime;//订单结束时间
    private int days;//工作总天数，前端计算不超过100天
}
