package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 创建群组
 * @date 2026/3/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupCreateDTO {
    //群组发起人id
    private Long initiator;
    //接受人id
    private Long accepter;
    //对应的招募id
    private Long recruitmentId;
    //发起的是什么请求，客服介入还是私聊，1为私聊，2为客服
    private Integer groupType;
}
