package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 客服分配类
 * @date 2026/3/31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CsGroupAssignmentResult {
    private Status status;   // 枚举：SUCCESS, SESSION_EXISTS, NO_AVAILABLE_CS
    private String csId;     // 当 status == SUCCESS 或 SESSION_EXISTS 时有效
    private String message;  // 可选描述

    public enum Status {
        SUCCESS,          // 成功分配新客服
        SESSION_EXISTS,   // 用户已有会话（可附带已有客服ID）
        NO_AVAILABLE_CS   // 无可用客服
    }
}