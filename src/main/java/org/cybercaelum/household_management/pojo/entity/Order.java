package org.cybercaelum.household_management.pojo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 订单实体类
 * @date 2026/2/18 下午9:23
 */
public class Order {
    private Long id;//主键
    private BigDecimal price;//价格
    private LocalDateTime orderTime;//下订单时间
    private Long recruitmentId;//订单对应的招募id
    private int status;//订单状态，0为取消，1为进行中，2为完成,3为待被雇者确认
    private LocalDateTime startTime;//订单开始时间
    private LocalDateTime endTime;//订单结束时间
    private Long employerId;//雇佣用户id
    private Long employee_id;//被雇佣用户id
}
