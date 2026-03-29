package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: openim用户相关回调接受类
 * @date 2026/3/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenimUserCallbackDTO {
    private String userID;//用户id
    private String callbackCommand;//回调命令
    private Integer platformID;//平台号
    private String platform;//平台名
}
