package org.cybercaelum.household_management.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 用户类
 * @date: 2025/10/15 下午8:14
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id; //主键
    private String phoneNumber; //手机号
    private String username; //用户名
    private String password; //密码
    private LocalDateTime createTime; //创建时间
    private Integer status; //账号状态，0为注销，1为启用
    private Integer role; //账号权限，0为管理员，1为用户，2客服，3机器人账号
    private String profileUrl; //头像地址
}
