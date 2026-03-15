package org.cybercaelum.household_management.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 订单详情VO
 * @date 2026/3/9 上午10:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailVO {
    private Long id;//主键
    private String orderNumber;//订单编号
    private LocalDateTime orderTime;//下单时间
    private LocalDate startTime;//订单开始时间
    private LocalDate endTime;//订单结束时间
    private Long employerId;//雇佣者id
    private Long employeeId;//被雇佣者id
    private BigDecimal price;//单价
    private Integer status;//订单状态
    private BigDecimal total;//总价
    private Integer days;//工作天数
    private String provinceName;//省份名称
    private String cityName;//城市名称
    private String districtName;//区县名称
    private String detail;//详细地址
    private Integer payStatus;//支付状态
    private LocalDateTime paymentTime;//支付时间
}
