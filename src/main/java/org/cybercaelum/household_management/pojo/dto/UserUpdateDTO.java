package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 用户更新传入信息
 * @date 2025/10/23 下午6:44
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserUpdateDTO {
    private Long id;//主键
    private String phoneNumber; //电话号
    private String username; //用户名
    private String password; //密码
    private String profileUrl; //头像地址
}
