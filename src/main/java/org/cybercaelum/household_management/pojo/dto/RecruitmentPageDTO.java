package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 分页查询招募
 * @date 2026/1/20 下午3:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecruitmentPageDTO implements Serializable {
    private int page;//页码
    private int pageSize;//页面大小
    private String keyWords;//关键词
    private BigDecimal mineSalary; //最小薪资
    private BigDecimal maxSalary; //最大薪资
    private int duration;//天数
    private String cityCode; //城市编号
    private String cityName; //城市名称
    private String districtCode; //区县编号
    private String districtName; //区县名称
    private BigDecimal targetSalary; // 目标薪资
}
