package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 结束会话请求DTO
 * @date 2026/3/31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionEndDTO implements Serializable {
    private Long userId;
    private Long csId;
    private String reason;  // 结束原因：USER_INITIATED, CS_INITIATED, TIMEOUT
    
    public static final String REASON_USER_INITIATED = "USER_INITIATED";
    public static final String REASON_CS_INITIATED = "CS_INITIATED";
    public static final String REASON_TIMEOUT = "TIMEOUT";
    public static final String REASON_CS_OFFLINE = "CS_OFFLINE";
}
