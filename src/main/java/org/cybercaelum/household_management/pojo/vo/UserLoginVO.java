package org.cybercaelum.household_management.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 用户登录返回信息
 * @date 2025/10/20 下午7:36
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserLoginVO {
    private String id; //主键
    private String username; //用户名
    private String profileUrl; //头像地址
    private String token; //token
}
