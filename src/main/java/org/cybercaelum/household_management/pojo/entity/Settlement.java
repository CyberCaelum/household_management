package org.cybercaelum.household_management.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 结算记录
 * @date 2026/3/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Settlement {
    private Long id;//主键
    private Long orderId;//订单id
    private Integer totalDays;//工作总天数
    private BigDecimal dailyRate;//日薪
    private BigDecimal totalAmount;//应付总金额
    private Integer defaultingParty;//违约方，1-雇主违约，2-雇员违约
    private BigDecimal penaltyDeduction;//违约金
    private BigDecimal finalAmount;//最终应付金额
    private Integer status;//结算状态：0-待结算，1-已结算，2-结算异常
    private LocalDateTime settlementTime;//结算时间
    private String orderNumber;//订单号
    private String refundNumber;//退款订单号
    private LocalDateTime createTime;//创建时间
}
