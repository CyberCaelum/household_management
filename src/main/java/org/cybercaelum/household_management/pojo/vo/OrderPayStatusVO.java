package org.cybercaelum.household_management.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 订单支付状态VO（供前端轮询查询）
 * @date: 2026/3/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "订单支付状态信息")
public class OrderPayStatusVO {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单号")
    private String orderNumber;

    @Schema(description = "支付状态：0未支付，1已支付，2已退款，3退款中")
    private Integer payStatus;

    @Schema(description = "支付状态描述")
    private String payStatusDesc;

    @Schema(description = "订单状态：0待付款，1已取消，2待被雇者确认，3已接单，4进行中，5已完成")
    private Integer orderStatus;

    @Schema(description = "订单状态描述")
    private String orderStatusDesc;

    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;

    @Schema(description = "支付时间（已支付时返回）")
    private LocalDateTime paymentTime;

    @Schema(description = "支付方式：1微信，2支付宝")
    private Integer payMethod;

    @Schema(description = "是否已支付成功")
    private Boolean paid;
}
