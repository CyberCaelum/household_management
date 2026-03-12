package org.cybercaelum.household_management.pojo.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cybercaelum.household_management.constant.MessageConstant;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 用户下订单
 * @date 2026/3/9 上午10:27
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrdersSubmitDTO {
    @NotNull(message = MessageConstant.RECRUITMENT_ID_EMPTY)
    private Long recruitmentId;//订单对应的招募id

    @NotNull(message = MessageConstant.PRICE_EMPTY)
    @DecimalMin(value = "0.0", inclusive = true, message = MessageConstant.PRICE_MIN)
    private BigDecimal price;//价格,需要在招募设定的最大和最小薪资内

    @NotNull(message = MessageConstant.START_TIME_EMPTY)
    private LocalDate startTime;//订单开始时间

    @NotNull(message = MessageConstant.END_TIME_EMPTY)
    private LocalDate endTime;//订单结束时间

    @Min(value = 1, message = MessageConstant.DAYS_RANGE_ERROR)
    @Max(value = 100, message = MessageConstant.DAYS_RANGE_ERROR)
    private int days;//工作总天数，前端计算不超过100天

    @AssertTrue(message = MessageConstant.ORDER_TIME_RANGE_ERROR)//时间范围错误
    private boolean isTimeRangeValid() {
        return startTime != null && endTime != null //时间都不为空
                && startTime.isAfter(LocalDate.now()) //开始时间大于现在时间
                && startTime.isBefore(endTime); //开始时间早于结束时间
    }
}
