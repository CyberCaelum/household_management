package org.cybercaelum.household_management.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 创建会话返回结果VO
 * @date 2026/3/1
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SessionCreateVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long sessionId;//会话主键
    private String openimSessionId;//OpenIM会话ID
    private Integer status;//会话状态
    
    // 雇主信息
    private Long employerId;//雇主id
    private String employerName;//雇主用户名
    private String employerAvatar;//雇主头像
    
    // 雇员信息
    private Long employeeId;//雇员id
    private String employeeName;//雇员用户名
    private String employeeAvatar;//雇员头像
}
