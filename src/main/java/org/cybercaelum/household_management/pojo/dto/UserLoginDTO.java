package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 用户登录传入信息
 * @date 2025/10/20 下午7:34
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserLoginDTO {
    private String phoneNumber; //电话号
    private String password; //密码
}
