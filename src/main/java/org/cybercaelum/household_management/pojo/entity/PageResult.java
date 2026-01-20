package org.cybercaelum.household_management.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 分页查询结果
 * @date 2026/1/20 下午4:24
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageResult implements Serializable {
    private Long total;//总数
    private List records; //当前页数据集合
}
