package org.cybercaelum.household_management.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.pojo.dto.OrdersPaymentDTO;
import org.cybercaelum.household_management.pojo.dto.OrdersSubmitDTO;
import org.cybercaelum.household_management.pojo.entity.PageResult;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.cybercaelum.household_management.pojo.vo.OrderPaymentVO;
import org.cybercaelum.household_management.pojo.vo.OrderSubmitVO;
import org.cybercaelum.household_management.pojo.vo.OrderVO;
import org.cybercaelum.household_management.service.OrderService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    /**
     * @description 用户下订单
     * @author CyberCaelum
     * @date 下午2:07 2026/3/11
     * @param ordersSubmitDTO 订单信息
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/submit")
    @Operation(summary = "用户下订单",description = "用户下订单")
    public Result submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("订单信息：{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * @description 用户支付
     * @author CyberCaelum
     * @date 上午9:28 2026/3/12
     * @param ordersPaymentDTO 支付信息
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @PutMapping("/payment")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "用户支付",description = "用户支付")
    public Result payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO){
        log.info("订单支付信息：{}",ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        return Result.success(orderPaymentVO);
    }
    //订单完成

    //订单结束
    //查看历史订单
    /**
     * @description 查看历史订单
     * @author CyberCaelum
     * @date 上午10:25 2026/3/12
     * @param page 页数
     * @param pageSize 页面大小
     * @param status 状态
     * @return org.cybercaelum.household_management.pojo.entity.Result<org.cybercaelum.household_management.pojo.entity.PageResult>
     **/
    @Operation(summary = "历史订单",description = "历史订单")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/history")
    public Result<PageResult> historyOrders(Integer page, Integer pageSize, Integer status){
        log.info("查看历史订单，page：{}，pageSize：{}，status：{}",page,pageSize,status);
        PageResult pageResult = orderService.history(page,pageSize,status);
        return Result.success(pageResult);
    }

    /**
     * @description 查看订单详情
     * @author CyberCaelum
     * @date 上午11:09 2026/3/13
     * @param id 订单id
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @Operation(summary = "订单详情",description = "订单详情")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/detail/{id}")
    public Result orderDetail(@PathVariable Long id){
        log.info("查看订单详情，orderId: {}",id);
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }
    //订单取消
    /**
     * @description 用户取消订单
     * @author CyberCaelum
     * @date 2026/3/15
         * @param id 订单id
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    public Result cancel(@PathVariable Long id) {
        log.info("用户取消订单orderId: {}",id);
        orderService.cancel(id);
        return Result.success();
    }
    //订单支付
}
