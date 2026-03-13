package org.cybercaelum.household_management.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bouncycastle.cms.PasswordRecipientId;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 订单返回信息
 * @date 2026/3/9 上午10:28
 */

public class OrderVO {
    private Long id;//主键
    private String orderNumber;//订单编号
    private LocalDateTime orderTime;//下单时间
    private LocalDate startTime;//订单开始时间
    private LocalDate endTime;//订单结束时间
    private Long employerId;//雇佣者id
    private Long employeeId;//被雇佣者id
    private BigDecimal price;//单价
    private int status;//订单状态
    private BigDecimal total;//总价
    private int days;//工作天数

}
