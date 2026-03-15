package org.cybercaelum.household_management.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 订单统计VO
 * @date 2026/3/9 上午10:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderStatisticsVO {
    private Integer toBeConfirmed;  // 待确认数量
    private Integer confirmed;      // 已确认数量
    private Integer inProgress;     // 进行中数量
    private Integer completed;      // 已完成数量
    private Integer cancelled;      // 已取消数量
}
