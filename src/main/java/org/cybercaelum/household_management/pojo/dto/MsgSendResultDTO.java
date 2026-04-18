package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 消息发送结果类
 * @date 2026/4/9 上午9:19
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MsgSendResultDTO {
    private String serverMsgID;
    private String clientMsgID;
    private Integer sendTime;
}
