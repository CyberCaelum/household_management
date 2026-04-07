package org.cybercaelum.household_management.pojo.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cybercaelum.household_management.constant.MessageConstant;

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
//    @NotEmpty(message = MessageConstant.USERNAME_EMPTY)//用户名不能为null或空字符串
//    @Size(max = 8, message = MessageConstant.USERNAME_TOO_LONG)//用户名长度最大为8个字符
//    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$", message = MessageConstant.USERNAME_STANDARD)//用户名只能包含字母、数字和中文
    private String username; //用户名

    @NotEmpty(message = MessageConstant.PASSWORD_EMPTY)//密码不能为null或空字符串
    @Size(min = 6, max = 18, message = MessageConstant.PASSWORD_LENGTH_RANGE)//密码长度在6到18个字符之间
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$",
            message = MessageConstant.PASSWORD_STANDARD
    )//密码必须包含数字、小写字母、大写字母和特殊字符
    private String password; //密码

    @NotEmpty(message = MessageConstant.PHONE_EMPTY)//手机号不能为null或空字符串
    @Size(min = 11, max = 11, message = MessageConstant.PHONE_LENGTH_STANDARD)//手机号长度必须为11位
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = MessageConstant.PHONE_STANDARD)//手机号必须符合中国手机号格式
    private String phoneNumber; //电话号码
}
