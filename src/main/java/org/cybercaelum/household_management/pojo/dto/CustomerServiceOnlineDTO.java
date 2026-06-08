package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 客服在线信息DTO
 * @date 2026/6/8 上午10:04
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerServiceOnlineDTO {
    private Long customerId;//客服id
    private String customerName;//客服名字
    private int session;//会话数量
}
