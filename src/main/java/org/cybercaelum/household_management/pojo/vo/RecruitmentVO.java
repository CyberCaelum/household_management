package org.cybercaelum.household_management.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 招募信息返回类
 * @date 2026/1/20 下午4:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentVO implements Serializable {
    private Long id; //主键
    private String title; //标题
    private BigDecimal mineSalary; //最低薪资
    private BigDecimal maxSalary; //最高薪资
    private LocalDate startTime; //开始时间
    private LocalDate endTime; //结束时间
    private String requirement; //要求
    private Integer status; //状态，0删除，1发布，2隐藏，3结束
    private String provinceCode; //省份编号
    private String provinceName; //省份名称
    private String cityCode; //城市编号
    private String cityName; //城市名称
    private String districtCode; //区县编号
    private String districtName; //区县名称
    private String detail; //详细地址信息
    private Long userId;//用户id
    private String username;//用户名
    private String profileUrl;//头像地址
    private LocalDateTime createTime; //创建时间
    private LocalDateTime updateTime; //更新时间
}
