package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 分页查询订单DTO
 * @date 2026/3/9 上午10:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersPageQueryDTO {
    private Integer page;
    private Integer pageSize;
    private Integer status;
    private String orderNumber;
    private String phone;
    private LocalDate beginTime;
    private LocalDate endTime;
}
