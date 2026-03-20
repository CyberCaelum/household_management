package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 支付超时消息（回调保底）
 * @date 2026/3/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayTimeoutMessage {
    private Long orderId;//订单id
    private String orderNumber;//订单号
    private LocalDateTime createTime;//创建时间
}
