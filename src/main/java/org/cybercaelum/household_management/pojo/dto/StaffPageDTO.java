package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 分页查询员工账号
 * @date 2026/3/24
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StaffPageDTO {
    private int page;//页数
    private int pageSize;//页码大小
    private String name;//用户名
    private Integer role;//角色
    private Integer status;//账号状态
}
