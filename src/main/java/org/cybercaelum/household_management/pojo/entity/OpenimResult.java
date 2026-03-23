package org.cybercaelum.household_management.pojo.entity;

import lombok.Data;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: Openim返回类
 * @date 2026/3/23
 */
@Data
public class OpenimResult<T> {
    private Integer errCode;
    private String errMsg;
    private String errDlt;
    private T data;
}
