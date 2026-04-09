package org.cybercaelum.household_management.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 客服统计VO
 * @date 2026/4/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CsStatisticsVO {
    // 在线客服数量
    private Integer onlineCsCount;
    // 当前会话数量
    private Integer currentSessionCount;
    // 等待队列人数
    private Integer waitingQueueCount;
    // 待处理争议数量
    private Integer pendingDisputeCount;
    // 当前客服的会话数量（如果查询者是客服）
    private Integer mySessionCount;
    // 当前客服的最大会话限制
    private Integer myMaxSessions;
}
