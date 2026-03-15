package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 取消订单DTO
 * @date 2026/3/9 上午10:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersCancelDTO {
    private Long id; // 订单ID
    private String cancelReason; // 取消原因
    private Integer cancelType; // 取消类型：1-协商一致，2-雇主强制，3-家政人员强制
}
