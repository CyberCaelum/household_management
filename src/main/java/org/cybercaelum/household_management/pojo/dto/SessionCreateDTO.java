package org.cybercaelum.household_management.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 创建会话请求DTO
 * @date 2026/3/1
 */
@Data
public class SessionCreateDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "招募ID不能为空")
    private Long recruitmentId;//招募id
}
