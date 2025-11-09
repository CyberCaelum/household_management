package org.cybercaelum.household_management.pojo.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cybercaelum.household_management.constant.MessageConstant;

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

    @NotEmpty(message = MessageConstant.USERNAME_EMPTY)
    @Size(max = 8, message = MessageConstant.USERNAME_TOO_LONG)
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$", message = MessageConstant.USERNAME_STANDARD)
    private String username; //用户名

    @NotEmpty(message = MessageConstant.PASSWORD_EMPTY)
    @Size(min = 6, max = 18, message = MessageConstant.PASSWORD_LENGTH_RANGE)
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$",
            message = MessageConstant.PASSWORD_STANDARD
    )
    private String password; //密码

    @NotEmpty(message = MessageConstant.PHONE_EMPTY)
    @Size(min = 11, max = 11, message = MessageConstant.PHONE_LENGTH_STANDARD)
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = MessageConstant.PHONE_STANDARD)
    private String phoneNumber; //电话号码

    private String profileUrl; //头像地址
}
