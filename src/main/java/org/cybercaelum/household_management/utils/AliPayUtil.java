package org.cybercaelum.household_management.utils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayFundTransToaccountTransferModel;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradeFastpayRefundQueryModel;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.properties.AliPayProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 支付宝支付工具类
 * 封装了支付宝支付的常用功能：PC网页支付、手机网站支付、扫码支付、订单查询、关闭订单、退款、退款查询等
 * 价格参数统一使用BigDecimal类型（单位：元）
 * @date 2026/3/17 下午5:05
 */
@Component
@Slf4j
public class AliPayUtil {

    @Autowired
    private AliPayProperties aliPayProperties;

    private AlipayClient alipayClient;

    /**
     * 初始化支付宝客户端
     */
    @PostConstruct
    public void init() {
        // 检查配置是否完整
        if (aliPayProperties.getAppId() == null || aliPayProperties.getAppId().isEmpty()) {
            log.warn("支付宝支付未配置，跳过初始化。请在配置文件中设置 alipay.app-id");
            return;
        }

        String gatewayUrl = aliPayProperties.getGatewayUrl();
        if (gatewayUrl == null || gatewayUrl.isEmpty()) {
            // 默认使用沙箱环境或正式环境
            gatewayUrl = Boolean.TRUE.equals(aliPayProperties.getSandbox())
                    ? "https://openapi-sandbox.dl.alipaydev.com/gateway.do"
                    : "https://openapi.alipay.com/gateway.do";
        }

        alipayClient = new DefaultAlipayClient(
                gatewayUrl,
                aliPayProperties.getAppId(),
                aliPayProperties.getAppPrivateKey(),
                aliPayProperties.getFormat(),
                aliPayProperties.getCharset(),
                aliPayProperties.getAlipayPublicKey(),
                aliPayProperties.getSignType()
        );
        log.info("支付宝支付服务初始化完成，环境: {}", Boolean.TRUE.equals(aliPayProperties.getSandbox()) ? "沙箱" : "正式");
    }

    /**
     * 检查支付宝支付是否已初始化
     */
    private void checkInitialized() {
        if (alipayClient == null) {
            throw new RuntimeException("支付宝支付服务未初始化，请检查配置 alipay");
        }
    }

    /**
     * 获取AlipayClient实例
     * @return AlipayClient
     */
    public AlipayClient getAlipayClient() {
        checkInitialized();
        return alipayClient;
    }

    /**
     * 创建PC网页支付订单（返回支付页面HTML表单）
     * @param orderNo 商户订单号
     * @param amount 金额（元）
     * @param subject 订单标题
     * @param body 订单描述（可选）
     * @return 支付页面HTML表单字符串
     */
    public String createPcPayPage(String orderNo, BigDecimal amount, String subject, String body) {
        checkInitialized();
        try {
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setNotifyUrl(aliPayProperties.getNotifyUrl());
            request.setReturnUrl(aliPayProperties.getReturnUrl());

            // 设置业务参数
            AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
            model.setOutTradeNo(orderNo);
            model.setTotalAmount(amount.toPlainString());
            model.setSubject(subject);
            model.setBody(body);
            model.setProductCode("FAST_INSTANT_TRADE_PAY");

            request.setBizModel(model);

            // 调用SDK生成表单
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if (response.isSuccess()) {
                log.info("PC网页支付订单创建成功，订单号: {}", orderNo);
                return response.getBody();
            } else {
                log.error("PC网页支付订单创建失败，订单号: {}, 错误: {} - {}", 
                        orderNo, response.getCode(), response.getMsg());
                throw new RuntimeException("创建支付订单失败: " + response.getMsg());
            }
        } catch (AlipayApiException e) {
            log.error("PC网页支付订单创建失败，订单号: {}, 错误: {}", orderNo, e.getMessage());
            throw new RuntimeException("创建支付订单失败: " + e.getMessage());
        }
    }

    /**
     * 创建手机网站支付订单（返回支付页面HTML表单）
     * @param orderNo 商户订单号
     * @param amount 金额（元）
     * @param subject 订单标题
     * @param body 订单描述（可选）
     * @return 支付页面HTML表单字符串
     */
    public String createWapPayPage(String orderNo, BigDecimal amount, String subject, String body) {
        checkInitialized();
        try {
            AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
            request.setNotifyUrl(aliPayProperties.getNotifyUrl());
            request.setReturnUrl(aliPayProperties.getReturnUrl());

            AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
            model.setOutTradeNo(orderNo);
            model.setTotalAmount(amount.toPlainString());
            model.setSubject(subject);
            model.setBody(body);
            model.setProductCode("QUICK_WAP_WAY");

            request.setBizModel(model);

            String form = alipayClient.pageExecute(request).getBody();
            log.info("手机网站支付订单创建成功，订单号: {}", orderNo);
            return form;
        } catch (AlipayApiException e) {
            log.error("手机网站支付订单创建失败，订单号: {}, 错误: {}", orderNo, e.getMessage());
            throw new RuntimeException("创建支付订单失败: " + e.getMessage());
        }
    }

    /**
     * 创建面对面扫码支付订单（返回支付二维码链接）
     * @param orderNo 商户订单号
     * @param amount 金额（元）
     * @param subject 订单标题
     * @param body 订单描述（可选）
     * @return 支付二维码链接
     */
    public String createPrecreatePay(String orderNo, BigDecimal amount, String subject, String body) {
        checkInitialized();
        try {
            AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
            request.setNotifyUrl(aliPayProperties.getNotifyUrl());

            AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
            model.setOutTradeNo(orderNo);
            model.setTotalAmount(amount.toPlainString());
            model.setSubject(subject);
            model.setBody(body);

            request.setBizModel(model);

            AlipayTradePrecreateResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                log.info("扫码支付订单创建成功，订单号: {}, 二维码: {}", orderNo, response.getQrCode());
                return response.getQrCode();
            } else {
                log.error("扫码支付订单创建失败，订单号: {}, 错误: {} - {}", 
                        orderNo, response.getCode(), response.getMsg());
                throw new RuntimeException("创建支付订单失败: " + response.getMsg());
            }
        } catch (AlipayApiException e) {
            log.error("扫码支付订单创建失败，订单号: {}, 错误: {}", orderNo, e.getMessage());
            throw new RuntimeException("创建支付订单失败: " + e.getMessage());
        }
    }

    /**
     * 查询订单状态
     * @param orderNo 商户订单号
     * @return 订单查询结果
     */
    public AlipayTradeQueryResponse queryOrder(String orderNo) {
        checkInitialized();
        try {
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();

            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            model.setOutTradeNo(orderNo);

            request.setBizModel(model);

            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                log.info("查询订单成功，订单号: {}, 状态: {}", orderNo, response.getTradeStatus());
            } else {
                log.warn("查询订单失败，订单号: {}, 错误: {} - {}", 
                        orderNo, response.getCode(), response.getMsg());
            }
            return response;
        } catch (AlipayApiException e) {
            log.error("查询订单失败，订单号: {}, 错误: {}", orderNo, e.getMessage());
            throw new RuntimeException("查询订单失败: " + e.getMessage());
        }
    }

    /**
     * 关闭订单
     * @param orderNo 商户订单号
     * @return 关闭结果
     */
    public AlipayTradeCloseResponse closeOrder(String orderNo) {
        checkInitialized();
        try {
            AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();

            AlipayTradeCloseModel model = new AlipayTradeCloseModel();
            model.setOutTradeNo(orderNo);

            request.setBizModel(model);

            AlipayTradeCloseResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                log.info("关闭订单成功，订单号: {}", orderNo);
            } else {
                log.error("关闭订单失败，订单号: {}, 错误: {} - {}", 
                        orderNo, response.getCode(), response.getMsg());
            }
            return response;
        } catch (AlipayApiException e) {
            log.error("关闭订单失败，订单号: {}, 错误: {}", orderNo, e.getMessage());
            throw new RuntimeException("关闭订单失败: " + e.getMessage());
        }
    }

    /**
     * 申请退款
     * @param orderNo 商户订单号
     * @param refundNo 商户退款单号
     * @param refundAmount 退款金额（元）
     * @param refundReason 退款原因
     * @return 退款结果
     */
    public AlipayTradeRefundResponse refund(String orderNo, String refundNo, BigDecimal refundAmount, String refundReason) {
        checkInitialized();
        try {
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();

            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            model.setOutTradeNo(orderNo);
            model.setRefundAmount(refundAmount.toPlainString());
            model.setRefundReason(refundReason);

            request.setBizModel(model);

            AlipayTradeRefundResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                log.info("申请退款成功，订单号: {}, 退款单号: {}", orderNo, refundNo);
            } else {
                log.error("申请退款失败，订单号: {}, 错误: {} - {}", 
                        orderNo, response.getCode(), response.getMsg());
            }
            return response;
        } catch (AlipayApiException e) {
            log.error("申请退款失败，订单号: {}, 退款单号: {}, 错误: {}", 
                    orderNo, refundNo, e.getMessage());
            throw new RuntimeException("申请退款失败: " + e.getMessage());
        }
    }

    /**
     * 查询退款状态
     * @param orderNo 商户订单号
     * @param refundNo 商户退款单号
     * @return 退款查询结果
     */
    public AlipayTradeFastpayRefundQueryResponse queryRefund(String orderNo, String refundNo) {
        checkInitialized();
        try {
            AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();

            AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
            model.setOutTradeNo(orderNo);
            model.setOutRequestNo(refundNo);

            request.setBizModel(model);

            AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                log.info("查询退款成功，订单号: {}, 退款单号: {}", orderNo, refundNo);
            } else {
                log.error("查询退款失败，订单号: {}, 退款单号: {}, 错误: {} - {}", 
                        orderNo, refundNo, response.getCode(), response.getMsg());
            }
            return response;
        } catch (AlipayApiException e) {
            log.error("查询退款失败，订单号: {}, 退款单号: {}, 错误: {}", 
                    orderNo, refundNo, e.getMessage());
            throw new RuntimeException("查询退款失败: " + e.getMessage());
        }
    }

    /**
     * 转账到支付宝账户
     * @param outBizNo 商户转账单号
     * @param payeeType 收款方账户类型（ALIPAY_LOGONID：支付宝登录号，ALIPAY_USERID：支付宝会员ID）
     * @param payeeAccount 收款方账户
     * @param amount 转账金额（元）
     * @param remark 转账备注
     * @return 转账结果
     */
    public AlipayFundTransToaccountTransferResponse transfer(String outBizNo, String payeeType, 
                                                              String payeeAccount, BigDecimal amount, String remark) {
        checkInitialized();
        try {
            AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();

            AlipayFundTransToaccountTransferModel model = new AlipayFundTransToaccountTransferModel();
            model.setOutBizNo(outBizNo);
            model.setPayeeType(payeeType);
            model.setPayeeAccount(payeeAccount);
            model.setAmount(amount.toPlainString());
            model.setRemark(remark);

            request.setBizModel(model);

            AlipayFundTransToaccountTransferResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                log.info("转账成功，商户单号: {}, 支付宝单号: {}", outBizNo, response.getOrderId());
            } else {
                log.error("转账失败，商户单号: {}, 错误: {} - {}", 
                        outBizNo, response.getCode(), response.getMsg());
            }
            return response;
        } catch (AlipayApiException e) {
            log.error("转账失败，商户单号: {}, 错误: {}", outBizNo, e.getMessage());
            throw new RuntimeException("转账失败: " + e.getMessage());
        }
    }

    /**
     * 验证支付宝回调通知签名
     * @param params 回调参数Map
     * @return 验证结果
     */
    public boolean verifyNotify(Map<String, String> params) {
        checkInitialized();
        try {
            return AlipaySignature.rsaCheckV1(params, 
                    aliPayProperties.getAlipayPublicKey(), 
                    aliPayProperties.getCharset(), 
                    aliPayProperties.getSignType());
        } catch (AlipayApiException e) {
            log.error("验证回调签名失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 生成支付回调成功响应
     * @return 响应字符串
     */
    public String buildNotifySuccessResponse() {
        return "success";
    }

    /**
     * 生成支付回调失败响应
     * @return 响应字符串
     */
    public String buildNotifyFailResponse() {
        return "fail";
    }
}
