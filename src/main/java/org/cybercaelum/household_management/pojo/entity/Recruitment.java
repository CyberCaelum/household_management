package org.cybercaelum.household_management.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author lordl
 * @version 1.0
 * @description: 招募信息
 * @date 2025/10/15 下午8:27
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Recruitment implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id; //主键
    private String title; //标题
    private BigDecimal mineSalary; //最低薪资
    private BigDecimal maxSalary; //最高薪资
    private LocalDateTime startTime; //开始时间
    private LocalDateTime endTime; //结束时间
    private String requirement; //要求
    private Integer status; //状态，0删除，1发布，2隐藏，3结束
    private String provinceCode; //省份编号
    private String provinceName; //省份名称
    private String cityCode; //城市编号
    private String cityName; //城市名称
    private String districtCode; //区县编号
    private String districtName; //区县名称
    private String detail; //详细地址信息
}
