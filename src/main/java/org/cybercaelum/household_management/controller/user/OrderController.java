package org.cybercaelum.household_management.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.pojo.dto.OrdersSubmitDTO;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.cybercaelum.household_management.pojo.vo.OrderSubmitVO;
import org.cybercaelum.household_management.service.OrderService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 订单Controller
 * @date 2026/3/9 上午10:21
 */
@RestController
@RequestMapping("/order")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "订单服务", description = "订单相关接口")
@Validated
public class OrderController {
    private final OrderService orderService;
    //下订单
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/submit")
    @Operation(summary = "用户下订单",description = "用户下订单")
    public Result submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("订单信息：{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);
        return Result.success();
    }
    //订单完成
    //订单结束
    //订单取消
    //订单支付
}
