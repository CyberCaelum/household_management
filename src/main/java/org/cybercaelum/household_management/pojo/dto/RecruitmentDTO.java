package org.cybercaelum.household_management.pojo.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cybercaelum.household_management.constant.MessageConstant;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 发布招募信息DTO
 * @date 2025/11/9 下午5:18
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RecruitmentDTO implements Serializable {

    private Long id; //主键

    @NotBlank(message = MessageConstant.TITLE_IS_EMPTY)//标题不能为空
    @Size(max = 30, message = MessageConstant.TITLE_TOO_LONG)//标题字数小于30
    private String title; //标题

    private BigDecimal mineSalary; //最低薪资

    //最高薪资小于等于10000.0
    @DecimalMax(value = "10000.0", inclusive = true, message = MessageConstant.MAX_SALARY_MAXIMUM)
    private BigDecimal maxSalary; //最高薪资

    private LocalDate startTime; //开始时间
    private LocalDate endTime; //结束时间

    @Size(max = 1000, message = MessageConstant.REQUIREMENT_TOO_LONG)//要求小于1000字
    private String requirement; //要求

    private Integer status; //状态，0删除，1发布，2隐藏，3结束
    private String provinceCode; //省份编号
    private String provinceName; //省份名称
    private String cityCode; //城市编号
    private String cityName; //城市名称
    private String districtCode; //区县编号
    private String districtName; //区县名称
    private String detail; //详细地址信息

    @AssertTrue(message = MessageConstant.SALARY_RANGE_ERROR)//薪资范围错误
    public Boolean isSalaryRangeValid(){
        return mineSalary != null && maxSalary != null //薪资都不为空
                && mineSalary.compareTo(new BigDecimal(0)) > 0 //最小薪资大于0
                && maxSalary.compareTo(mineSalary) > 0; //最大薪资大于最小薪资
    }

    @AssertTrue(message = MessageConstant.TIME_RANGE_ERROR)//时间范围错误
    public Boolean isTimeRangeValid(){
        return startTime != null && endTime != null //时间都不为空
                && startTime.isAfter(LocalDate.now()) //开始时间大于现在时间
                && startTime.isBefore(endTime); //开始时间早于结束时间
    }

    @AssertTrue(message = MessageConstant.STATUS_ERROR)//状态错误
    public Boolean isStatusValid(){
        return status != null && (status == 0 || status == 1 || status == 2 || status == 3);
    }

    @AssertTrue(message = MessageConstant.ADDRESS_ERROR) //地址错误
    public Boolean isAddressValid(){
        return provinceCode != null && provinceName != null &&
                cityCode != null && cityName != null && detail != null;
    }
}
