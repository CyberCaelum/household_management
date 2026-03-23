package org.cybercaelum.household_management.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cybercaelum.household_management.constant.MessageConstant;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 添加聊天机器人类
 * @date 2026/3/23
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OpenimBootAddDTO {
    @NotNull(message = MessageConstant.USERNAME_EMPTY)
    private String nickName;//昵称
    private String faceUrl;//通知号头像
}
