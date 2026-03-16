package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 付款
 * @date 2026/3/9 上午10:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersPaymentDTO {
    private Long orderId;//订单id
    private String orderNumber;//订单号
    private Integer payMethod;//支付方法
}
