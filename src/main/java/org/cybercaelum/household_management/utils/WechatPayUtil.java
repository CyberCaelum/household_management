package org.cybercaelum.household_management.utils;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayOrderCloseRequest;
import com.github.binarywang.wxpay.bean.request.WxPayOrderQueryRequest;
import com.github.binarywang.wxpay.bean.request.WxPayRefundQueryRequest;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayOrderCloseResult;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundQueryResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.properties.WechatPayProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 微信支付工具类
 * 封装了微信支付的常用功能：统一下单、订单查询、关闭订单、退款、退款查询、回调处理等
 * @date 2026/3/12 上午10:25
 */
@Component
@Slf4j
public class WechatPayUtil {

    @Autowired
    private WechatPayProperties wechatPayProperties;

    private WxPayService wxPayService;

    /**
     * 初始化微信支付服务
     */
    @PostConstruct
    public void init() {
        // 检查配置是否完整
        if (wechatPayProperties.getMchId() == null || wechatPayProperties.getMchId().isEmpty()) {
            log.warn("微信支付未配置，跳过初始化。请在配置文件中设置 wechat.pay.mch-id");
            return;
        }
        
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(wechatPayProperties.getAppId());
        payConfig.setMchId(wechatPayProperties.getMchId());
        payConfig.setApiV3Key(wechatPayProperties.getApiV3Key());
        payConfig.setCertSerialNo(wechatPayProperties.getMchSerialNo());
        payConfig.setNotifyUrl(wechatPayProperties.getNotifyUrl());
        
        // 设置私钥
        String privateKeyPath = wechatPayProperties.getPrivateKeyPath();
        if (privateKeyPath != null && !privateKeyPath.isEmpty()) {
            File privateKeyFile = new File(privateKeyPath);
            if (privateKeyFile.exists()) {
                payConfig.setPrivateKeyPath(privateKeyPath);
            } else {
                log.warn("微信支付私钥文件不存在: {}", privateKeyPath);
            }
        }
        
        // 设置沙箱环境
        payConfig.setUseSandboxEnv(wechatPayProperties.getSandbox());

        wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        log.info("微信支付服务初始化完成");
    }

    /**
     * 获取WxPayService实例
     * @return WxPayService
     */
    public WxPayService getWxPayService() {
        checkInitialized();
        return wxPayService;
    }
    
    /**
     * 检查微信支付是否已初始化
     */
    private void checkInitialized() {
        if (wxPayService == null) {
            throw new RuntimeException("微信支付服务未初始化，请检查配置 wechat.pay");
        }
    }

    /**
     * 创建Native支付（扫码支付）订单
     * @param orderNo 商户订单号
     * @param amount 金额（单位：分）
     * @param description 商品描述
     * @return 支付二维码链接
     */
    public String createNativeOrder(String orderNo, int amount, String description) {
        checkInitialized();
        try {
            WxPayUnifiedOrderRequest request = WxPayUnifiedOrderRequest.newBuilder()
                    .body(description)
                    .outTradeNo(orderNo)
                    .totalFee(amount)
                    .spbillCreateIp(getLocalIp())
                    .tradeType(WxPayConstants.TradeType.NATIVE)
                    .notifyUrl(wechatPayProperties.getNotifyUrl())
                    .build();

            WxPayUnifiedOrderResult result = wxPayService.unifiedOrder(request);
            log.info("Native支付订单创建成功，订单号: {}, codeUrl: {}", orderNo, result.getCodeURL());
            return result.getCodeURL();
        } catch (WxPayException e) {
            log.error("Native支付订单创建失败，订单号: {}, 错误: {}", orderNo, e.getMessage());
            throw new RuntimeException("创建支付订单失败: " + e.getMessage());
        }
    }

    /**
     * 创建JSAPI支付订单（微信公众号/小程序支付）
     * @param orderNo 商户订单号
     * @param amount 金额（单位：分）
     * @param description 商品描述
     * @param openId 用户OpenID
     * @return 调起支付的参数（前端需使用此参数调用wx.chooseWXPay）
     */
    public String createJsapiOrder(String orderNo, int amount, String description, String openId) {
        checkInitialized();
        try {
            WxPayUnifiedOrderRequest request = WxPayUnifiedOrderRequest.newBuilder()
                    .body(description)
                    .outTradeNo(orderNo)
                    .totalFee(amount)
                    .spbillCreateIp(getLocalIp())
                    .tradeType(WxPayConstants.TradeType.JSAPI)
                    .openid(openId)
                    .notifyUrl(wechatPayProperties.getNotifyUrl())
                    .build();

            WxPayUnifiedOrderResult result = wxPayService.unifiedOrder(request);
            log.info("JSAPI支付订单创建成功，订单号: {}, prepayId: {}", orderNo, result.getPrepayId());
            return result.getPrepayId();
        } catch (WxPayException e) {
            log.error("JSAPI支付订单创建失败，订单号: {}, 错误: {}", orderNo, e.getMessage());
            throw new RuntimeException("创建支付订单失败: " + e.getMessage());
        }
    }

    /**
     * 创建APP支付订单
     * @param orderNo 商户订单号
     * @param amount 金额（单位：分）
     * @param description 商品描述
     * @return 调起支付的参数
     */
    public String createAppOrder(String orderNo, int amount, String description) {
        checkInitialized();
        try {
            WxPayUnifiedOrderRequest request = WxPayUnifiedOrderRequest.newBuilder()
                    .body(description)
                    .outTradeNo(orderNo)
                    .totalFee(amount)
                    .spbillCreateIp(getLocalIp())
                    .tradeType(WxPayConstants.TradeType.APP)
                    .notifyUrl(wechatPayProperties.getNotifyUrl())
                    .build();

            WxPayUnifiedOrderResult result = wxPayService.unifiedOrder(request);
            log.info("APP支付订单创建成功，订单号: {}, prepayId: {}", orderNo, result.getPrepayId());
            return result.getPrepayId();
        } catch (WxPayException e) {
            log.error("APP支付订单创建失败，订单号: {}, 错误: {}", orderNo, e.getMessage());
            throw new RuntimeException("创建支付订单失败: " + e.getMessage());
        }
    }

    /**
     * 创建H5支付订单
     * @param orderNo 商户订单号
     * @param amount 金额（单位：分）
     * @param description 商品描述
     * @param sceneType 场景类型（iOS/Android/WAP）
     * @return 支付跳转链接
     */
    public String createH5Order(String orderNo, int amount, String description, String sceneType) {
        checkInitialized();
        try {
            WxPayUnifiedOrderRequest request = WxPayUnifiedOrderRequest.newBuilder()
                    .body(description)
                    .outTradeNo(orderNo)
                    .totalFee(amount)
                    .spbillCreateIp(getLocalIp())
                    .tradeType(WxPayConstants.TradeType.MWEB)
                    .notifyUrl(wechatPayProperties.getNotifyUrl())
                    .sceneInfo("{\"h5_info\":{\"type\":\"" + sceneType + "\"}}")
                    .build();

            WxPayUnifiedOrderResult result = wxPayService.unifiedOrder(request);
            log.info("H5支付订单创建成功，订单号: {}, mwebUrl: {}", orderNo, result.getMwebUrl());
            return result.getMwebUrl();
        } catch (WxPayException e) {
            log.error("H5支付订单创建失败，订单号: {}, 错误: {}", orderNo, e.getMessage());
            throw new RuntimeException("创建支付订单失败: " + e.getMessage());
        }
    }

    /**
     * 查询订单状态
     * @param orderNo 商户订单号
     * @return 订单查询结果
     */
    public WxPayOrderQueryResult queryOrder(String orderNo) {
        checkInitialized();
        try {
            WxPayOrderQueryRequest request = WxPayOrderQueryRequest.newBuilder()
                    .outTradeNo(orderNo)
                    .build();

            WxPayOrderQueryResult result = wxPayService.queryOrder(request);
            log.info("查询订单成功，订单号: {}, 状态: {}", orderNo, result.getTradeState());
            return result;
        } catch (WxPayException e) {
            log.error("查询订单失败，订单号: {}, 错误: {}", orderNo, e.getMessage());
            throw new RuntimeException("查询订单失败: " + e.getMessage());
        }
    }

    /**
     * 关闭订单
     * @param orderNo 商户订单号
     * @return 关闭结果
     */
    public WxPayOrderCloseResult closeOrder(String orderNo) {
        checkInitialized();
        try {
            WxPayOrderCloseRequest request = WxPayOrderCloseRequest.newBuilder()
                    .outTradeNo(orderNo)
                    .build();

            WxPayOrderCloseResult result = wxPayService.closeOrder(request);
            log.info("关闭订单成功，订单号: {}", orderNo);
            return result;
        } catch (WxPayException e) {
            log.error("关闭订单失败，订单号: {}, 错误: {}", orderNo, e.getMessage());
            throw new RuntimeException("关闭订单失败: " + e.getMessage());
        }
    }

    /**
     * 申请退款
     * @param orderNo 商户订单号
     * @param refundNo 商户退款单号
     * @param totalAmount 订单总金额（单位：分）
     * @param refundAmount 退款金额（单位：分）
     * @param reason 退款原因
     * @return 退款结果
     */
    public WxPayRefundResult refund(String orderNo, String refundNo, int totalAmount, int refundAmount, String reason) {
        checkInitialized();
        try {
            WxPayRefundRequest request = WxPayRefundRequest.newBuilder()
                    .outTradeNo(orderNo)
                    .outRefundNo(refundNo)
                    .totalFee(totalAmount)
                    .refundFee(refundAmount)
                    .refundDesc(reason)
                    .notifyUrl(wechatPayProperties.getRefundNotifyUrl())
                    .build();

            WxPayRefundResult result = wxPayService.refund(request);
            log.info("申请退款成功，订单号: {}, 退款单号: {}, 状态: {}", 
                    orderNo, refundNo, result.getResultCode());
            return result;
        } catch (WxPayException e) {
            log.error("申请退款失败，订单号: {}, 退款单号: {}, 错误: {}", 
                    orderNo, refundNo, e.getMessage());
            throw new RuntimeException("申请退款失败: " + e.getMessage());
        }
    }

    /**
     * 查询退款状态
     * @param refundNo 商户退款单号
     * @return 退款查询结果
     */
    public WxPayRefundQueryResult queryRefund(String refundNo) {
        checkInitialized();
        try {
            WxPayRefundQueryRequest request = WxPayRefundQueryRequest.newBuilder()
                    .outRefundNo(refundNo)
                    .build();

            WxPayRefundQueryResult result = wxPayService.refundQuery(request);
            log.info("查询退款成功，退款单号: {}", refundNo);
            return result;
        } catch (WxPayException e) {
            log.error("查询退款失败，退款单号: {}, 错误: {}", refundNo, e.getMessage());
            throw new RuntimeException("查询退款失败: " + e.getMessage());
        }
    }

    /**
     * 解析支付回调通知
     * @param xmlData 回调的XML数据
     * @return 解析后的结果
     */
    public WxPayOrderNotifyResult parsePayNotify(String xmlData) {
        checkInitialized();
        try {
            WxPayOrderNotifyResult result = wxPayService.parseOrderNotifyResult(xmlData);
            log.info("解析支付回调成功，订单号: {}, 微信订单号: {}", 
                    result.getOutTradeNo(), result.getTransactionId());
            return result;
        } catch (WxPayException e) {
            log.error("解析支付回调失败: {}", e.getMessage());
            throw new RuntimeException("解析支付回调失败: " + e.getMessage());
        }
    }

    /**
     * 解析退款回调通知
     * @param xmlData 回调的XML数据
     * @return 解析后的结果
     */
    public WxPayRefundNotifyResult parseRefundNotify(String xmlData) {
        checkInitialized();
        try {
            WxPayRefundNotifyResult result = wxPayService.parseRefundNotifyResult(xmlData);
            log.info("解析退款回调成功");
            return result;
        } catch (WxPayException e) {
            log.error("解析退款回调失败: {}", e.getMessage());
            throw new RuntimeException("解析退款回调失败: " + e.getMessage());
        }
    }

    /**
     * 生成支付回调成功响应
     * @return 响应XML
     */
    public String buildNotifySuccessResponse() {
        return WxPayNotifyResponse.success("OK");
    }

    /**
     * 生成支付回调失败响应
     * @param msg 错误消息
     * @return 响应XML
     */
    public String buildNotifyFailResponse(String msg) {
        return WxPayNotifyResponse.fail(msg);
    }

    /**
     * 获取本机IP地址
     * @return IP地址
     */
    private String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("获取本机IP失败，使用默认IP");
            return "127.0.0.1";
        }
    }
}
