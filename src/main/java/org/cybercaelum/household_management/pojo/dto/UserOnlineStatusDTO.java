package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 用户在线状态信息
 * @date 2026/3/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOnlineStatusDTO {
    private String userID;//用户id
    private Integer status;//用户在线状态，在线：1，离线：0
}
