package org.cybercaelum.household_management.controller.common;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.constant.PayMethodConstant;
import org.cybercaelum.household_management.service.OrderService;
import org.cybercaelum.household_management.utils.WechatPayUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 微信支付回调控制器
 * 处理微信支付的异步通知
 * @date 2026/3/17
 */
@RestController
@RequestMapping("/api/pay")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "支付回调", description = "微信支付结果回调接口")
public class PayNotifyController {

    private final WechatPayUtil wechatPayUtil;
    private final OrderService orderService;

    /**
     * @description 微信支付结果回调
     * 用户支付成功后，微信会调用此接口通知支付结果
     * @author CyberCaelum
     * @date 2026/3/17
     * @param request HTTP请求
     * @return 响应XML字符串
     **/
    @PostMapping("/notify")
    @Operation(summary = "微信支付回调", description = "微信支付结果异步通知")
    public String payNotify(HttpServletRequest request) {
        log.info("收到微信支付回调通知");
        try {
            // 1. 读取微信发送的XML数据
            String xmlData = readRequestBody(request);
            log.debug("回调数据: {}", xmlData);

            // 2. 解析回调数据（工具类会自动验证签名）
            WxPayOrderNotifyResult result = wechatPayUtil.parsePayNotify(xmlData);

            // 3. 判断支付是否成功
            if ("SUCCESS".equals(result.getResultCode()) && "SUCCESS".equals(result.getReturnCode())) {
                String orderNo = result.getOutTradeNo();  // 商户订单号
                String transactionId = result.getTransactionId();  // 微信订单号
                Integer totalFee = result.getTotalFee();  // 支付金额（分）

                log.info("支付成功，订单号: {}, 微信订单号: {}, 金额: {}分", orderNo, transactionId, totalFee);

                // 4. 更新订单状态（幂等处理在 paySuccess 方法中）
                orderService.paySuccess(orderNo, PayMethodConstant.WECHAT_PAY);

                // 5. 返回成功响应给微信（必须返回成功，否则微信会重复通知）
                return wechatPayUtil.buildNotifySuccessResponse();
            } else {
                // 支付失败或通信失败
                String errCode = result.getErrCode();
                String errCodeDes = result.getErrCodeDes();
                log.error("支付回调返回失败，订单号: {}, 错误码: {}, 错误描述: {}", 
                        result.getOutTradeNo(), errCode, errCodeDes);
                return wechatPayUtil.buildNotifyFailResponse("支付失败: " + errCodeDes);
            }
        } catch (Exception e) {
            log.error("处理支付回调异常", e);
            return wechatPayUtil.buildNotifyFailResponse("系统异常: " + e.getMessage());
        }
    }

    /**
     * @description 微信退款结果回调
     * 退款成功后，微信会调用此接口通知退款结果
     * @author CyberCaelum
     * @date 2026/3/20
     * @param request HTTP请求
     * @return 响应XML字符串
     **/
    @PostMapping("/refund/notify")
    @Operation(summary = "微信退款回调", description = "微信退款结果异步通知")
    public String refundNotify(HttpServletRequest request) {
        log.info("收到微信退款回调通知");
        try {
            // 1. 读取微信发送的XML数据
            String xmlData = readRequestBody(request);
            log.debug("退款回调数据: {}", xmlData);

            // 2. 解析回调数据（工具类会自动验证签名）
            WxPayRefundNotifyResult result = wechatPayUtil.parseRefundNotify(xmlData);
            
            // 3. 获取退款详情（通过getReqInfo()获取）
            WxPayRefundNotifyResult.ReqInfo reqInfo = result.getReqInfo();
            if (reqInfo == null) {
                log.error("退款回调解析失败，reqInfo为空");
                return wechatPayUtil.buildNotifyFailResponse("解析退款信息失败");
            }

            // 4. 判断退款是否成功
            if ("SUCCESS".equals(reqInfo.getRefundStatus())) {
                String orderNo = reqInfo.getOutTradeNo();  // 商户订单号
                String refundNo = reqInfo.getOutRefundNo();  // 商户退款单号
                String refundId = reqInfo.getRefundId();  // 微信退款单号
                Integer refundFee = reqInfo.getSettlementRefundFee();  // 退款金额（分）

                log.info("退款成功，订单号: {}, 退款单号: {}, 微信退款单号: {}, 金额: {}分", 
                        orderNo, refundNo, refundId, refundFee);

                // 5. 处理退款成功（幂等处理在 refundSuccess 方法中）
                orderService.refundSuccess(orderNo, refundNo, refundFee);

                // 6. 返回成功响应给微信（必须返回成功，否则微信会重复通知）
                return wechatPayUtil.buildNotifySuccessResponse();
            } else {
                // 退款失败
                String refundStatus = reqInfo.getRefundStatus();
                log.error("退款回调返回失败，订单号: {}, 退款状态: {}", 
                        reqInfo.getOutTradeNo(), refundStatus);
                return wechatPayUtil.buildNotifyFailResponse("退款状态: " + refundStatus);
            }
        } catch (Exception e) {
            log.error("处理退款回调异常", e);
            return wechatPayUtil.buildNotifyFailResponse("系统异常: " + e.getMessage());
        }
    }

    /**
     * @description 读取请求体中的XML数据
     * @author CyberCaelum
     * @date 2026/3/17
     * @param request HTTP请求
     * @return XML字符串
     **/
    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
