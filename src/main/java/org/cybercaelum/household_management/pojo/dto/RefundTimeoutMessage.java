package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 退款超时消息
 * @date 2026/3/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundTimeoutMessage {
    private Long orderId;//订单id
    private String refundNumber;//退款单号
    private LocalDateTime createTime;//创建时间
}
