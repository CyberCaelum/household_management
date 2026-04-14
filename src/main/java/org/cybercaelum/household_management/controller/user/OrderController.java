package org.cybercaelum.household_management.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.pojo.dto.*;
import org.cybercaelum.household_management.pojo.entity.PageResult;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.cybercaelum.household_management.pojo.vo.*;
import org.cybercaelum.household_management.service.OrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("订单信息：{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * @description 生成微信支付二维码
     * @author CyberCaelum
     * @date 下午4:19 2026/3/17
     * @param ordersPaymentDTO 订单信息
     * @return org.cybercaelum.household_management.pojo.entity.Result<java.lang.String>
     **/
    @PutMapping("/pay-native")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "用户支付",description = "用户支付")
    public Result<String> payNative(@RequestBody OrdersPaymentDTO ordersPaymentDTO){
        log.info("订单支付信息：{}",ordersPaymentDTO);
        String codeUrl = orderService.nativeOrder(ordersPaymentDTO);
        return Result.success(codeUrl);
    }

    /**
     * @description 模拟支付成功（仅用于开发测试）
     * @author CyberCaelum
     * @date 2026/4/13
     * @param orderId 订单ID
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @Operation(summary = "模拟支付成功", description = "开发测试用，直接让订单变为已支付")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/mockPay/{orderId}")
    public Result mockPaySuccess(@PathVariable Long orderId) {
        log.info("模拟支付成功，orderId: {}", orderId);
        orderService.mockPaySuccess(orderId);
        return Result.success();
    }

    /**
     * @description 查询订单支付状态（供前端轮询）
     * @author CyberCaelum
     * @date 2026/3/24
     * @param orderId 订单ID
     * @return org.cybercaelum.household_management.pojo.entity.Result<org.cybercaelum.household_management.pojo.vo.OrderPayStatusVO>
     **/
    @Operation(summary = "查询支付状态", description = "查询订单支付状态，供前端轮询使用")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/payStatus/{orderId}")
    public Result<OrderPayStatusVO> queryPayStatus(@PathVariable Long orderId) {
        log.info("查询订单支付状态，orderId: {}", orderId);
        OrderPayStatusVO payStatusVO = orderService.queryPayStatus(orderId);
        return Result.success(payStatusVO);
    }

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
    public Result<OrderVO> orderDetail(@PathVariable Long id){
        log.info("查看订单详情，orderId: {}",id);
        OrderVO orderVO = orderService.detail(id);
        return Result.success(orderVO);
    }

//    /**
//     * @description 用户取消订单
//     * @author CyberCaelum
//     * @date 2026/3/15
//     * @param id 订单id
//     * @return org.cybercaelum.household_management.pojo.entity.Result
//     **/
//    @Operation(summary = "取消订单", description = "用户发起取消申请")
//    @SecurityRequirement(name = "bearerAuth")
//    @PostMapping("/cancel/{id}")
//    public Result cancel(@PathVariable Long id,@RequestBody String reason) {
//        log.info("用户取消订单orderId: {}",id);
//        orderService.cancel(id,reason);
//        return Result.success();
//    }

    /**
     * @description 被雇者接单
     * @author CyberCaelum
     * @date 2026/3/15
     * @param ordersConfirmDTO 接单信息
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @Operation(summary = "接单", description = "被雇者确认接单")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/confirm")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("被雇者接单，orderId: {}", ordersConfirmDTO.getId());
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * @description 被雇者拒单
     * @author CyberCaelum
     * @date 2026/3/15
     * @param ordersRejectionDTO 拒单信息
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @Operation(summary = "拒单", description = "被雇者拒绝接单")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/rejection")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        log.info("被雇者拒单，orderId: {}", ordersRejectionDTO.getId());
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * @description 开始服务（将订单标记为进行中）
     * @author CyberCaelum
     * @date 2026/3/15
     * @param id 订单id
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @Operation(summary = "开始服务", description = "标记订单为服务中")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/delivery/{id}")
    public Result delivery(@PathVariable Long id) {
        log.info("开始服务，orderId: {}", id);
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * @description 完成订单
     * @author CyberCaelum
     * @date 2026/3/15
     * @param id 订单id
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @Operation(summary = "完成订单", description = "标记订单为已完成")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable Long id) {
        log.info("完成订单，orderId: {}", id);
        orderService.complete(id);
        return Result.success();
    }

//    /**
//     * @description 再来一单
//     * @author CyberCaelum
//     * @date 2026/3/15
//     * @param id 订单id
//     * @return org.cybercaelum.household_management.pojo.entity.Result
//     **/
//    @Operation(summary = "再来一单", description = "根据历史订单创建新订单")
//    @SecurityRequirement(name = "bearerAuth")
//    @PostMapping("/repetition/{id}")
//    public Result repetition(@PathVariable Long id) {
//        log.info("再来一单，orderId: {}", id);
//        orderService.repetition(id);
//        return Result.success();
//    }

//    /**
//     * @description 查看订单统计
//     * @author CyberCaelum
//     * @date 2026/3/15
//     * @return org.cybercaelum.household_management.pojo.entity.Result
//     **/
//    @Operation(summary = "订单统计", description = "各状态订单数量统计")
//    @SecurityRequirement(name = "bearerAuth")
//    @GetMapping("/statistics")
//    public Result<OrderStatisticsVO> statistics() {
//        log.info("查看订单统计");
//        OrderStatisticsVO statistics = orderService.statistics();
//        return Result.success(statistics);
//    }

//    /**
//     * @description 订单搜索
//     * @author CyberCaelum
//     * @date 2026/3/15
//     * @param ordersPageQueryDTO 查询条件
//     * @return org.cybercaelum.household_management.pojo.entity.Result
//     **/
//    @Operation(summary = "订单搜索", description = "条件搜索订单")
//    @SecurityRequirement(name = "bearerAuth")
//    @GetMapping("/conditionSearch")
//    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
//        log.info("订单搜索，条件: {}", ordersPageQueryDTO);
//        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
//        return Result.success(pageResult);
//    }

    // ==================== 每日确认相关 ====================

    /**
     * @description 家政人员每日服务完成确认
     * @author CyberCaelum
     * @date 2026/3/15
     * @param orderId 订单id
     * @param serviceDate 服务日期
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @Operation(summary = "每日服务确认", description = "家政人员标记今日服务完成")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/dailyConfirm/worker")
    public Result workerDailyConfirm(@RequestParam Long orderId, 
                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate serviceDate) {
        log.info("家政人员确认服务完成，orderId: {}，serviceDate: {}", orderId, serviceDate);
        orderService.workerDailyConfirm(orderId, serviceDate);
        return Result.success();
    }

    /**
     * @description 雇主确认每日服务
     * @author CyberCaelum
     * @date 2026/3/15
     * @param confirmationId 确认记录id
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @Operation(summary = "确认每日服务", description = "雇主确认每日服务完成")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/dailyConfirm/employer/{confirmationId}")
    public Result employerDailyConfirm(@PathVariable Long confirmationId) {
        log.info("雇主确认服务，confirmationId: {}", confirmationId);
        orderService.employerDailyConfirm(confirmationId);
        return Result.success();
    }

    /**
     * @description 雇主对每日服务提出争议
     * @author CyberCaelum
     * @date 2026/3/15
     * @param confirmationId 确认记录id
     * @param reason 争议原因
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @Operation(summary = "服务争议", description = "雇主对每日服务提出争议")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/dailyConfirm/dispute/{confirmationId}")
    public Result employerDisputeDaily(@PathVariable Long confirmationId, @RequestParam String reason) {
        log.info("雇主提出服务争议，confirmationId: {}，reason: {}", confirmationId, reason);
        orderService.employerDisputeDaily(confirmationId, reason);
        return Result.success();
    }

    // ==================== 取消申请相关 ====================

    /**
     * @description 发起取消申请
     * @author CyberCaelum
     * @date 2026/3/15
     * @param ordersCancelDTO 取消信息
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @Operation(summary = "发起取消申请", description = "发起协商或强制取消申请")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/applyCancel")
    public Result applyCancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("发起取消申请，orderId: {}，cancelType: {}", ordersCancelDTO.getId(), ordersCancelDTO.getCancelType());
        orderService.applyCancel(ordersCancelDTO.getId(), ordersCancelDTO.getCancelType(), ordersCancelDTO.getCancelReason());
        return Result.success();
    }

    /**
     * @description 响应取消申请
     * @author CyberCaelum
     * @date 2026/3/15
     * @param applicationId 申请id
     * @param agree 是否同意
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @Operation(summary = "响应取消申请", description = "同意或拒绝取消申请")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/respondCancel/{applicationId}")
    public Result respondCancelApplication(@PathVariable Long applicationId, @RequestParam Boolean agree) {
        log.info("响应取消申请，applicationId: {}，agree: {}", applicationId, agree);
        orderService.respondCancelApplication(applicationId, agree);
        return Result.success();
    }
}
