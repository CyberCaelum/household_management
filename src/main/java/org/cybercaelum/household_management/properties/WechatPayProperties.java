package org.cybercaelum.household_management.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 微信支付配置属性类
 * @date 2026/3/12 上午10:20
 */
@Component
@ConfigurationProperties(prefix = "wechat.pay")
@Data
public class WechatPayProperties {

    /** 应用ID */
    private String appId;

    /** 商户号 */
    private String mchId;

    /** 商户API密钥 */
    private String apiV3Key;

    /** 商户证书序列号 */
    private String mchSerialNo;

    /** 商户私钥路径（apiclient_key.pem） */
    private String privateKeyPath;

    /** 通知地址 */
    private String notifyUrl;

    /** 退款通知地址 */
    private String refundNotifyUrl;

    /** 是否沙箱环境 */
    private Boolean sandbox = false;

    /** 是否Mock模式（开发测试用，不调用真实微信接口） */
    private Boolean mock = false;
}
