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
 * @description: 会话信息
 * @date 2026/3/1
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OpenimGroup implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;//主键
    private Long recruitmentId;//招募id
    private Long employeeId;//雇员id
    private Long employerId;//雇主id，发布招募的用户
    private String openimGroupId;//使用商品id_雇主id_雇员id拼接
    private Integer status;//会话状态，0为结束，1为活动
    private Integer groupType;//群组种类,1私聊，2客服
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间
}
