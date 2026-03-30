package org.cybercaelum.household_management.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: Openim返回类
 * @date 2026/3/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenimResult<T> {
    private Integer errCode;//错误码
    private String errMsg;//错误信息
    private String errDlt;//详细错误信息
    private T data;
}
