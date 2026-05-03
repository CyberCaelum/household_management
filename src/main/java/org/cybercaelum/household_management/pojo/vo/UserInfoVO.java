package org.cybercaelum.household_management.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 用户信息VO
 * @date 2025/10/23 下午8:00
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserInfoVO {
    private Long id; //主键
    private String phoneNumber; //手机号
    private String username; //用户名
    private LocalDateTime createTime; //创建时间
    private Integer status; //账号状态，0为注销，1为启用
    private Integer role; //账号权限，0为管理员，1为用户，2客服，3机器人账号
    private String profileUrl; //头像地址
}
