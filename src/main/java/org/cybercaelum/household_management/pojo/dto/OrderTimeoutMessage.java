package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 订单超时消息
 * @date 2026/3/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderTimeoutMessage {
    private Long Id;//订单id
    private Long userId;//用户id
    private BigDecimal amount;//订单金额
    private LocalDateTime createTime;//创建时间
}
