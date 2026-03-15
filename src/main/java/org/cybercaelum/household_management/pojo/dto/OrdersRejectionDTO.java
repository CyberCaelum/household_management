package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 拒单DTO
 * @date 2026/3/9 上午10:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersRejectionDTO {
    private Long id; // 订单ID
    private String rejectionReason; // 拒单原因
}
