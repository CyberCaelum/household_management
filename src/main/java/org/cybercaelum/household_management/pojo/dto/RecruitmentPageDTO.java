package org.cybercaelum.household_management.pojo.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cybercaelum.household_management.constant.MessageConstant;

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
    private String title;//关键词

    @DecimalMin(value = "0.0", inclusive = true, message = MessageConstant.MIN_SALARY_MINIMUM)//最低薪资不能低于0
    @NotNull(message = MessageConstant.MIN_SALARY_IS_NULL)  // 非空验证
    private BigDecimal mineSalary; //最小薪资

    @DecimalMax(value = "10000.0", inclusive = true, message = MessageConstant.MAX_SALARY_MAXIMUM)//最高薪资小于等于10000.0
    @NotNull(message = MessageConstant.MAX_SALARY_IS_NULL)  // 非空验证
    private BigDecimal maxSalary; //最大薪资

    private int durationType;//天数,1 = 7天以内，2 = 15天以内，3 = 30天以内，4 = 60天以内，5 = 60 天以外
    private String cityCode; //城市编号
    private String cityName; //城市名称
    private String districtCode; //区县编号
    private String districtName; //区县名称

    @AssertTrue(message = MessageConstant.SALARY_RANGE_ERROR)//薪资范围错误
    private boolean isSalaryRangeValid() {
        if (mineSalary == null || maxSalary == null) {
            return true; // 由 @NotNull 处理
        }
        return mineSalary.compareTo(BigDecimal.ZERO) > 0
                && maxSalary.compareTo(mineSalary) > 0;
    }

    @AssertTrue(message = MessageConstant.DURATION_TYPE_ERROR)//天数错误
    private boolean isDurationTypeValid(){
        return durationType == 0 || durationType == 1 || durationType == 2 || durationType == 3
                || durationType == 4 || durationType == 5;
    }
}
