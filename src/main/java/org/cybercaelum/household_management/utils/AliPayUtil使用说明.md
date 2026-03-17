# AliPayUtil 支付宝支付工具类使用说明

## 一、简介

`AliPayUtil` 是封装了支付宝支付功能的工具类，提供以下功能：
- PC网页支付
- 手机网站支付
- 面对面扫码支付
- 订单查询
- 关闭订单
- 退款申请与查询
- 转账功能
- 回调通知验证

**金额单位：元（使用 `BigDecimal` 类型）**

---

## 二、配置说明

### 1. 添加依赖

`pom.xml` 中已添加：

```xml
<dependency>
    <groupId>com.alipay.sdk</groupId>
    <artifactId>alipay-sdk-java</artifactId>
    <version>4.38.10.ALL</version>
</dependency>
```

### 2. 配置文件

在 `application.yml` 或 `application.properties` 中添加：

```yaml
alipay:
  app-id: 你的应用ID
  app-private-key: 你的应用私钥（PKCS1格式）
  alipay-public-key: 支付宝公钥
  notify-url: https://你的域名/api/pay/alipay/notify
  return-url: https://你的域名/pay/result
  gateway-url: https://openapi-sandbox.dl.alipaydev.com/gateway.do  # 沙箱环境
  # gateway-url: https://openapi.alipay.com/gateway.do              # 正式环境
  charset: UTF-8
  sign-type: RSA2
  format: JSON
  sandbox: true   # true=沙箱环境，false=正式环境
```

### 3. 密钥获取方式

1. 登录 [支付宝开放平台](https://open.alipay.com/)
2. 进入"控制台" -> "应用"
3. 选择对应应用，进入"开发设置"
4. 获取 `APPID`、`应用私钥`、`支付宝公钥`

---

## 三、基础使用示例

### 注入工具类

```java
@Service
public class OrderService {
    
    @Autowired
    private AliPayUtil aliPayUtil;
    
    // ...
}
```

---

## 四、各功能详细说明

### 1. PC网页支付

适用于电脑浏览器访问的支付场景。

```java
/**
 * 创建PC网页支付
 * @param orderNo 商户订单号（需唯一）
 * @param amount 支付金额（元，BigDecimal）
 * @param subject 订单标题
 * @param body 订单描述
 * @return HTML表单字符串，直接返回给前端
 */
public String pcPay() {
    String orderNo = "ORDER" + System.currentTimeMillis();
    BigDecimal amount = new BigDecimal("99.99");
    String subject = "商品购买";
    String body = "商品详情描述";
    
    // 返回HTML表单，前端直接展示或提交
    String htmlForm = aliPayUtil.createPcPayPage(orderNo, amount, subject, body);
    return htmlForm;
}
```

**前端处理：**

```html
<!-- 后端返回的HTML表单直接插入页面 -->
<div id="payForm"></div>
<script>
    document.getElementById('payForm').innerHTML = htmlForm;
    // 自动提交表单
    document.forms[0].submit();
</script>
```

---

### 2. 手机网站支付

适用于手机浏览器访问的支付场景。

```java
/**
 * 创建手机网站支付
 */
public String wapPay() {
    String orderNo = "ORDER" + System.currentTimeMillis();
    BigDecimal amount = new BigDecimal("99.99");
    String subject = "商品购买";
    String body = "商品详情描述";
    
    String htmlForm = aliPayUtil.createWapPayPage(orderNo, amount, subject, body);
    return htmlForm;
}
```

---

### 3. 扫码支付（面对面支付）

适用于生成二维码，用户扫码支付的场景。

```java
/**
 * 创建扫码支付
 * @return 二维码链接，前端生成二维码图片
 */
public String qrcodePay() {
    String orderNo = "ORDER" + System.currentTimeMillis();
    BigDecimal amount = new BigDecimal("99.99");
    String subject = "商品购买";
    String body = "商品详情描述";
    
    // 返回二维码链接，格式如：https://qr.alipay.com/xxx
    String qrCode = aliPayUtil.createPrecreatePay(orderNo, amount, subject, body);
    return qrCode;
}
```

**前端生成二维码：**

```html
<!-- 使用qrcode.js等库生成二维码 -->
<div id="qrcode"></div>
<script>
    new QRCode(document.getElementById("qrcode"), {
        text: qrCodeUrl,  // 后端返回的二维码链接
        width: 200,
        height: 200
    });
</script>
```

---

### 4. 查询订单状态

```java
/**
 * 查询订单状态
 */
public void queryOrder(String orderNo) {
    AlipayTradeQueryResponse response = aliPayUtil.queryOrder(orderNo);
    
    if (response.isSuccess()) {
        String tradeStatus = response.getTradeStatus();
        // WAIT_BUYER_PAY - 交易创建，等待买家付款
        // TRADE_CLOSED - 未付款交易超时关闭，或支付完成后全额退款
        // TRADE_SUCCESS - 交易支付成功
        // TRADE_FINISHED - 交易结束，不可退款
        
        String tradeNo = response.getTradeNo();      // 支付宝交易号
        BigDecimal totalAmount = new BigDecimal(response.getTotalAmount());
        
        System.out.println("订单状态: " + tradeStatus);
        System.out.println("支付宝交易号: " + tradeNo);
        System.out.println("支付金额: " + totalAmount);
    }
}
```

---

### 5. 关闭订单

```java
/**
 * 关闭未支付订单
 */
public void closeOrder(String orderNo) {
    AlipayTradeCloseResponse response = aliPayUtil.closeOrder(orderNo);
    
    if (response.isSuccess()) {
        System.out.println("订单关闭成功");
    } else {
        System.out.println("订单关闭失败: " + response.getMsg());
    }
}
```

---

### 6. 申请退款

```java
/**
 * 申请退款
 * @param orderNo 原订单号
 * @param refundNo 退款单号（需唯一）
 * @param refundAmount 退款金额（元）
 * @param reason 退款原因
 */
public void refund(String orderNo) {
    String refundNo = "REFUND" + System.currentTimeMillis();
    BigDecimal refundAmount = new BigDecimal("50.00");
    String reason = "商品质量问题";
    
    AlipayTradeRefundResponse response = aliPayUtil.refund(
        orderNo, refundNo, refundAmount, reason
    );
    
    if (response.isSuccess()) {
        System.out.println("退款申请成功");
        System.out.println("退款金额: " + response.getRefundFee());
    } else {
        System.out.println("退款失败: " + response.getMsg());
    }
}
```

---

### 7. 查询退款状态

```java
/**
 * 查询退款状态
 */
public void queryRefund(String orderNo, String refundNo) {
    AlipayTradeFastpayRefundQueryResponse response = 
        aliPayUtil.queryRefund(orderNo, refundNo);
    
    if (response.isSuccess()) {
        String refundStatus = response.getRefundStatus();
        BigDecimal refundAmount = new BigDecimal(response.getRefundAmount());
        
        System.out.println("退款状态: " + refundStatus);
        System.out.println("退款金额: " + refundAmount);
    }
}
```

---

### 8. 转账到支付宝账户

```java
/**
 * 转账到支付宝账户
 * @param outBizNo 商户转账单号
 * @param payeeType 收款方类型：ALIPAY_LOGONID（登录号）或 ALIPAY_USERID（会员ID）
 * @param payeeAccount 收款方账号
 * @param amount 转账金额
 * @param remark 转账备注
 */
public void transfer() {
    String outBizNo = "TRANSFER" + System.currentTimeMillis();
    String payeeType = "ALIPAY_LOGONID";    // 或 ALIPAY_USERID
    String payeeAccount = "user@example.com";  // 对方支付宝账号
    BigDecimal amount = new BigDecimal("100.00");
    String remark = "服务费结算";
    
    AlipayFundTransToaccountTransferResponse response = 
        aliPayUtil.transfer(outBizNo, payeeType, payeeAccount, amount, remark);
    
    if (response.isSuccess()) {
        System.out.println("转账成功，支付宝订单号: " + response.getOrderId());
    } else {
        System.out.println("转账失败: " + response.getMsg());
    }
}
```

---

## 五、回调通知处理

### 1. 回调接口示例

```java
@RestController
@RequestMapping("/api/pay/alipay")
@Slf4j
public class AliPayNotifyController {
    
    @Autowired
    private AliPayUtil aliPayUtil;
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 支付宝支付异步通知
     */
    @PostMapping("/notify")
    public String payNotify(HttpServletRequest request) {
        // 1. 获取所有回调参数
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = String.join(",", values);
            params.put(name, valueStr);
        }
        
        // 2. 验证签名
        boolean signVerified = aliPayUtil.verifyNotify(params);
        
        if (!signVerified) {
            log.error("支付宝回调签名验证失败");
            return aliPayUtil.buildNotifyFailResponse();
        }
        
        // 3. 处理业务逻辑
        String orderNo = params.get("out_trade_no");      // 商户订单号
        String tradeNo = params.get("trade_no");          // 支付宝交易号
        String tradeStatus = params.get("trade_status");  // 交易状态
        String totalAmount = params.get("total_amount");  // 订单金额
        
        log.info("收到支付宝回调，订单号: {}, 状态: {}", orderNo, tradeStatus);
        
        // 4. 根据交易状态处理
        if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
            // 支付成功，更新订单状态
            orderService.handlePaySuccess(orderNo, tradeNo, new BigDecimal(totalAmount));
        }
        
        // 5. 返回成功响应（必须返回"success"，否则支付宝会重复通知）
        return aliPayUtil.buildNotifySuccessResponse();
    }
}
```

### 2. 回调参数说明

| 参数名 | 说明 |
|--------|------|
| out_trade_no | 商户订单号 |
| trade_no | 支付宝交易号 |
| trade_status | 交易状态（WAIT_BUYER_PAY/TRADE_CLOSED/TRADE_SUCCESS/TRADE_FINISHED）|
| total_amount | 订单金额 |
| buyer_id | 买家支付宝用户号 |
| gmt_payment | 交易付款时间 |
| receipt_amount | 实收金额 |

---

## 六、BigDecimal 使用注意事项

### 1. 创建BigDecimal对象

```java
// ✅ 推荐：使用字符串构造
BigDecimal amount1 = new BigDecimal("99.99");

// ❌ 不推荐：使用double构造，可能有精度问题
BigDecimal amount2 = new BigDecimal(99.99);

// ✅ 从其他类型转换
BigDecimal amount3 = BigDecimal.valueOf(99.99);  // 内部使用Double.toString
Integer fen = 9999;
BigDecimal amount4 = new BigDecimal(fen).divide(new BigDecimal("100"));
```

### 2. 金额计算

```java
BigDecimal amount = new BigDecimal("100");
BigDecimal discount = new BigDecimal("10.5");

// 加法
BigDecimal sum = amount.add(discount);      // 110.5

// 减法
BigDecimal sub = amount.subtract(discount); // 89.5

// 乘法
BigDecimal mul = amount.multiply(new BigDecimal("0.8"));  // 80

// 除法（必须指定精度和舍入模式）
BigDecimal div = amount.divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);  // 33.33
```

### 3. 比较大小

```java
BigDecimal a = new BigDecimal("100");
BigDecimal b = new BigDecimal("99.99");

// 比较（返回-1, 0, 1）
int result = a.compareTo(b);  // 1

// 判断是否相等（不要用equals）
if (a.compareTo(b) == 0) {
    // 相等
}

// 判断是否大于
if (a.compareTo(b) > 0) {
    // a > b
}
```

---

## 七、沙箱环境测试

### 1. 沙箱账号获取

1. 登录 [支付宝开放平台](https://open.alipay.com/)
2. 进入"开发工具" -> "沙箱"
3. 获取沙箱应用的 `APPID`、`应用私钥`、`支付宝公钥`
4. 获取沙箱买家/卖家账号用于测试

### 2. 沙箱支付测试

1. 使用沙箱环境的买家账号登录 [沙箱钱包APP](https://sandbox.alipaydev.com/)
2. 扫描二维码或访问支付页面
3. 使用沙箱支付密码完成支付

### 3. 沙箱环境配置

```yaml
alipay:
  app-id: 沙箱APPID
  app-private-key: 沙箱应用私钥
  alipay-public-key: 沙箱支付宝公钥
  gateway-url: https://openapi-sandbox.dl.alipaydev.com/gateway.do
  sandbox: true
```

---

## 八、常见问题

### Q1: 签名验证失败
- 检查应用私钥和支付宝公钥是否匹配
- 检查密钥格式（需要PKCS1格式）
- 检查字符编码是否为UTF-8

### Q2: 回调通知收不到
- 检查服务器是否有公网IP
- 检查notify-url是否可访问
- 检查防火墙设置
- 使用内网穿透工具（如ngrok）进行本地调试

### Q3: 金额精度问题
- 始终使用 `BigDecimal` 处理金额
- 创建时使用字符串构造器：`new BigDecimal("99.99")`
- 除法运算时指定精度和舍入模式

### Q4: 订单重复处理
- 回调通知可能多次发送，需要做好幂等处理
- 根据订单号判断是否已经处理过

---

## 九、相关文档

- [支付宝开放平台](https://open.alipay.com/)
- [支付宝沙箱环境](https://sandbox.alipaydev.com/)
- [电脑网站支付文档](https://opendocs.alipay.com/open/270/105899/)
- [手机网站支付文档](https://opendocs.alipay.com/open/203/105288/)
- [面对面支付文档](https://opendocs.alipay.com/open/194/105072/)

---

## 十、版本记录

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0 | 2026-03-17 | 初始版本，实现基础支付功能 |
