package org.cybercaelum.household_management.pojo.entity;

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
 * @description: 订单实体类
 * @date 2026/2/18 下午9:23
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Order {
    private Long id;//主键
    private BigDecimal price;//价格,单价
    private LocalDateTime orderTime;//下订单时间
    private Long recruitmentId;//订单对应的招募id
    private int status;//订单状态，0为取消，1为进行中，2为完成,3为待被雇者确认
    private LocalDate startTime;//订单开始时间
    private LocalDate endTime;//订单结束时间
    private Long employerId;//雇佣用户id
    private Long employee_id;//被雇佣用户id
    private String provinceCode; //省份编号
    private String provinceName; //省份名称
    private String cityCode; //城市编号
    private String cityName; //城市名称
    private String districtCode; //区县编号
    private String districtName; //区县名称
    private String detail; //详细地址信息
    private BigDecimal total;//总价
    private int days;//工作总天数
    private String orderNumber;//订单号
}
