package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 等待队列中的用户信息
 * @date 2026/3/31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WaitingUserDTO implements Serializable {
    private Long userId;
    private Long requestTime;
}
