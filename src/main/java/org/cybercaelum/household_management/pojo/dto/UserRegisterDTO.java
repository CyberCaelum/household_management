package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 用户注册传入类
 * @date 2025/10/20 下午8:40
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRegisterDTO {
    private String username; //用户名
    private String password; //密码
    private String phoneNumber; //电话号码
}
