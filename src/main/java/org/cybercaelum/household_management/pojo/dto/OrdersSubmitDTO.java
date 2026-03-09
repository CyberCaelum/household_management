package org.cybercaelum.household_management.pojo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 用户下订单
 * @date 2026/3/9 上午10:27
 */
public class OrdersSubmitDTO {
    private Long recruitmentId;//订单对应的招募id
    private BigDecimal price;//价格
    private LocalDateTime startTime;//订单开始时间
    private LocalDateTime endTime;//订单结束时间
}
