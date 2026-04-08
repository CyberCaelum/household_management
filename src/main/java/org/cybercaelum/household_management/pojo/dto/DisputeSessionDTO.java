package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: TODO
 * @date 2026/4/8 上午8:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisputeSessionDTO {
    private Long userId;//发起人id
    private Long orderId;//订单id
    private Long dailyConfirmationId;//每日确定id
}
