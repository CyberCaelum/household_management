package org.cybercaelum.household_management.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 用户下订单
 * @date 2026/3/9 上午10:26
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderSubmitVO {
    private Long id;//订单id
    private String orderNumber;//订单号
    private BigDecimal orderAmount;//订单金额
    private LocalDateTime orderTime;//下单时间

}
