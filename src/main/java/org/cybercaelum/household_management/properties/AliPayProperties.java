package org.cybercaelum.household_management.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 支付宝支付配置属性类
 * @date 2026/3/17 下午5:00
 */
@Component
@ConfigurationProperties(prefix = "alipay")
@Data
public class AliPayProperties {

    /** 应用ID */
    private String appId;

    /** 应用私钥 */
    private String appPrivateKey;

    /** 支付宝公钥 */
    private String alipayPublicKey;

    /** 支付异步通知地址 */
    private String notifyUrl;

    /** 支付同步返回地址 */
    private String returnUrl;

    /** 支付宝网关地址（沙箱/正式环境） */
    private String gatewayUrl;

    /** 编码格式，默认UTF-8 */
    private String charset = "UTF-8";

    /** 签名类型，默认RSA2 */
    private String signType = "RSA2";

    /** 返回数据格式，默认JSON */
    private String format = "JSON";

    /** 是否沙箱环境 */
    private Boolean sandbox = false;
}
