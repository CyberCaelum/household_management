package org.cybercaelum.household_management.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.asm.SpringAsmInfo;

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
    private Integer status;//订单状态，0待付款，1为取消，2为待被雇者确认,3已接单,4为进行中，5为完成,
    private LocalDate startTime;//订单开始时间
    private LocalDate endTime;//订单结束时间
    private Long employerId;//雇佣用户id
    private Long employeeId;//被雇佣用户id
    private String provinceCode; //省份编号
    private String provinceName; //省份名称
    private String cityCode; //城市编号
    private String cityName; //城市名称
    private String districtCode; //区县编号
    private String districtName; //区县名称
    private String detail; //详细地址信息
    private BigDecimal total;//总价
    private Integer days;//工作总天数
    private String orderNumber;//订单号
    private Integer payMethod;//支付方式，1微信，2支付宝
    private Integer payStatus;//支付状态，0未支付，1已支付，2已退款
    private String cancelReason;//订单取消原因
    private String rejectionReason;//订单拒绝原因
    private LocalDateTime cancelTime;//订单取消时间
    private LocalDateTime paymentTime;//支付时间
    private LocalDateTime rejectionTime;//订单拒绝时间
    private LocalDateTime refundTime;//退款时间
    private Integer cancelType;//取消类型，0未取消，1协商一致取消，2雇主强制取消，3家政人员强制取消，4平台取消
    private BigDecimal heldAmount;//托管金额，雇员应得金额
    private String refundNumber;//退款单号
}
