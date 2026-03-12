# 微信支付工具类使用文档

## 概述

`WechatPayUtil` 是基于 [binarywang/weixin-java-pay](https://github.com/binarywang/weixin-java-pay) SDK 封装的微信支付工具类，提供了统一下单、订单查询、关闭订单、退款、回调处理等常用功能。

---

## 目录

1. [快速开始](#快速开始)
2. [配置说明](#配置说明)
3. [功能列表](#功能列表)
4. [使用示例](#使用示例)
5. [API 参考](#api-参考)
6. [注意事项](#注意事项)

---

## 快速开始

### 1. 添加依赖

在 `pom.xml` 中添加微信支付 SDK 依赖：

```xml
<dependency>
    <groupId>com.github.binarywang</groupId>
    <artifactId>weixin-java-pay</artifactId>
    <version>4.6.0</version>
</dependency>
```

### 2. 配置文件

在 `application.yml` 或 `application.properties` 中添加微信支付配置：

```yaml
wechat:
  pay:
    app-id: wxappid                          # 应用ID
    mch-id: 1234567890                       # 商户号
    api-v3-key: your_api_v3_key              # API v3 密钥
    mch-serial-no: your_cert_serial_no       # 商户证书序列号
    private-key-path: /path/to/apiclient_key.pem  # 商户私钥文件路径
    notify-url: https://your-domain.com/pay/callback      # 支付结果通知地址
    refund-notify-url: https://your-domain.com/pay/refund/callback  # 退款通知地址
    sandbox: false                           # 是否使用沙箱环境
```

### 3. 注入使用

```java
@Autowired
private WechatPayUtil wechatPayUtil;
```

---

## 配置说明

### 配置属性类

配置属性由 `WechatPayProperties` 类管理，支持以下配置项：

| 属性名 | 必填 | 说明 |
|--------|------|------|
| `app-id` | 是 | 微信应用ID |
| `mch-id` | 是 | 微信支付商户号 |
| `api-v3-key` | 是 | API v3 密钥（用于签名验证） |
| `mch-serial-no` | 是 | 商户证书序列号 |
| `private-key-path` | 是 | 商户私钥文件路径（apiclient_key.pem） |
| `notify-url` | 是 | 支付结果通知地址 |
| `refund-notify-url` | 否 | 退款结果通知地址 |
| `sandbox` | 否 | 是否使用沙箱环境，默认 `false` |

---

## 功能列表

| 功能 | 方法名 | 适用场景 |
|------|--------|----------|
| Native支付 | `createNativeOrder()` | PC网站扫码支付 |
| JSAPI支付 | `createJsapiOrder()` | 微信公众号/小程序支付 |
| APP支付 | `createAppOrder()` | 移动端APP支付 |
| H5支付 | `createH5Order()` | 手机浏览器支付 |
| 订单查询 | `queryOrder()` | 查询订单状态 |
| 关闭订单 | `closeOrder()` | 取消未支付订单 |
| 申请退款 | `refund()` | 发起退款请求 |
| 查询退款 | `queryRefund()` | 查询退款状态 |
| 解析回调 | `parsePayNotify()` / `parseRefundNotify()` | 处理微信通知 |

---

## 使用示例

### 1. Native 支付（扫码支付）

生成支付二维码，用户微信扫码完成支付。

```java
@PostMapping("/createNativeOrder")
public Result<String> createNativeOrder(@RequestBody OrdersPaymentDTO dto) {
    // 生成订单号
    String orderNo = generateOrderNo();
    
    // 创建Native支付订单（金额单位：分）
    String codeUrl = wechatPayUtil.createNativeOrder(
        orderNo,
        dto.getAmount() * 100,  // 元转分
        "家政服务订单"
    );
    
    // 返回二维码链接，前端生成二维码展示
    return Result.success(codeUrl);
}
```

### 2. JSAPI 支付（公众号/小程序）

用户在微信公众号或小程序内完成支付。

```java
@PostMapping("/createJsapiOrder")
public Result<String> createJsapiOrder(@RequestBody OrdersPaymentDTO dto, @RequestAttribute("userId") Long userId) {
    // 获取用户OpenID（需提前获取并存储）
    String openId = userService.getOpenId(userId);
    
    // 创建JSAPI支付订单
    String prepayId = wechatPayUtil.createJsapiOrder(
        dto.getOrderNumber(),
        dto.getAmount() * 100,
        "家政服务订单",
        openId
    );
    
    // 返回prepayId，前端使用此参数调起微信支付
    return Result.success(prepayId);
}
```

前端调用示例：
```javascript
wx.requestPayment({
    timeStamp: '',
    nonceStr: '',
    package: 'prepay_id=' + prepayId,
    signType: 'RSA',
    paySign: '',
    success: function(res) {
        // 支付成功
    },
    fail: function(res) {
        // 支付失败
    }
});
```

### 3. APP 支付

移动端APP应用内调起微信支付。

```java
@PostMapping("/createAppOrder")
public Result<String> createAppOrder(@RequestBody OrdersPaymentDTO dto) {
    String prepayId = wechatPayUtil.createAppOrder(
        dto.getOrderNumber(),
        dto.getAmount() * 100,
        "家政服务订单"
    );
    
    return Result.success(prepayId);
}
```

### 4. H5 支付

手机浏览器中调起微信支付。

```java
@PostMapping("/createH5Order")
public Result<String> createH5Order(@RequestBody OrdersPaymentDTO dto) {
    // 场景类型：iOS, Android, WAP
    String mwebUrl = wechatPayUtil.createH5Order(
        dto.getOrderNumber(),
        dto.getAmount() * 100,
        "家政服务订单",
        "iOS"
    );
    
    // 返回支付跳转链接，前端跳转至此URL
    return Result.success(mwebUrl);
}
```

### 5. 查询订单状态

```java
@GetMapping("/queryOrder/{orderNo}")
public Result<OrderPaymentVO> queryOrder(@PathVariable String orderNo) {
    WxPayOrderQueryResult result = wechatPayUtil.queryOrder(orderNo);
    
    OrderPaymentVO vo = new OrderPaymentVO();
    vo.setOrderNo(result.getOutTradeNo());
    vo.setTransactionId(result.getTransactionId());
    vo.setTradeState(result.getTradeState());  // SUCCESS/CLOSED/NOTPAY/REFUND等
    vo.setTotalFee(result.getTotalFee());
    vo.setTimeEnd(result.getTimeEnd());
    
    return Result.success(vo);
}
```

订单状态说明：
- `SUCCESS` - 支付成功
- `REFUND` - 转入退款
- `NOTPAY` - 未支付
- `CLOSED` - 已关闭
- `REVOKED` - 已撤销（仅付款码支付）
- `USERPAYING` - 用户支付中（仅付款码支付）
- `PAYERROR` - 支付失败

### 6. 关闭订单

```java
@PostMapping("/closeOrder/{orderNo}")
public Result<Void> closeOrder(@PathVariable String orderNo) {
    wechatPayUtil.closeOrder(orderNo);
    return Result.success();
}
```

### 7. 申请退款

```java
@PostMapping("/refund")
public Result<Void> refund(@RequestBody RefundDTO dto) {
    // 生成退款单号
    String refundNo = generateRefundNo();
    
    WxPayRefundResult result = wechatPayUtil.refund(
        dto.getOrderNo(),           // 原订单号
        refundNo,                   // 退款单号
        dto.getTotalAmount() * 100, // 订单总金额（分）
        dto.getRefundAmount() * 100,// 退款金额（分）
        dto.getReason()             // 退款原因
    );
    
    return Result.success();
}
```

### 8. 查询退款状态

```java
@GetMapping("/queryRefund/{refundNo}")
public Result<RefundVO> queryRefund(@PathVariable String refundNo) {
    WxPayRefundQueryResult result = wechatPayUtil.queryRefund(refundNo);
    
    RefundVO vo = new RefundVO();
    vo.setRefundNo(result.getOutRefundNo());
    vo.setRefundFee(result.getRefundFee());
    // ... 其他字段
    
    return Result.success(vo);
}
```

### 9. 处理支付回调

微信支付完成后，微信服务器会主动通知商户服务器。

```java
@PostMapping("/callback")
public String payCallback(@RequestBody String xmlData) {
    try {
        // 解析回调数据
        WxPayOrderNotifyResult result = wechatPayUtil.parsePayNotify(xmlData);
        
        // 获取订单信息
        String orderNo = result.getOutTradeNo();
        String transactionId = result.getTransactionId();
        Integer totalFee = result.getTotalFee();
        String timeEnd = result.getTimeEnd();
        
        // TODO: 处理业务逻辑
        // 1. 验证订单金额是否与商户侧一致
        // 2. 更新订单状态为已支付
        // 3. 记录交易流水
        // 4. 发送通知等
        
        orderService.paySuccess(orderNo, transactionId);
        
        // 返回成功响应给微信
        return wechatPayUtil.buildNotifySuccessResponse();
        
    } catch (Exception e) {
        log.error("支付回调处理失败: {}", e.getMessage());
        // 返回失败响应，微信会重新发送通知
        return wechatPayUtil.buildNotifyFailResponse("处理失败");
    }
}
```

### 10. 处理退款回调

```java
@PostMapping("/refund/callback")
public String refundCallback(@RequestBody String xmlData) {
    try {
        WxPayRefundNotifyResult result = wechatPayUtil.parseRefundNotify(xmlData);
        
        // TODO: 处理退款成功逻辑
        // 1. 更新退款记录状态
        // 2. 更新订单状态（如有需要）
        
        return wechatPayUtil.buildNotifySuccessResponse();
        
    } catch (Exception e) {
        log.error("退款回调处理失败: {}", e.getMessage());
        return wechatPayUtil.buildNotifyFailResponse("处理失败");
    }
}
```

---

## API 参考

### 核心方法

#### `createNativeOrder(String orderNo, int amount, String description)`
创建 Native 支付订单（扫码支付）。

**参数：**
- `orderNo` - 商户订单号（32字符内）
- `amount` - 金额（单位：分）
- `description` - 商品描述

**返回：** 支付二维码链接（`code_url`），前端生成二维码供用户扫码

---

#### `createJsapiOrder(String orderNo, int amount, String description, String openId)`
创建 JSAPI 支付订单（公众号/小程序）。

**参数：**
- `orderNo` - 商户订单号
- `amount` - 金额（单位：分）
- `description` - 商品描述
- `openId` - 用户在当前公众号/小程序的 OpenID

**返回：** 预支付交易会话标识（`prepay_id`）

---

#### `createAppOrder(String orderNo, int amount, String description)`
创建 APP 支付订单。

**参数：**
- `orderNo` - 商户订单号
- `amount` - 金额（单位：分）
- `description` - 商品描述

**返回：** 预支付交易会话标识（`prepay_id`）

---

#### `createH5Order(String orderNo, int amount, String description, String sceneType)`
创建 H5 支付订单。

**参数：**
- `orderNo` - 商户订单号
- `amount` - 金额（单位：分）
- `description` - 商品描述
- `sceneType` - 场景类型：`iOS`、`Android`、`WAP`

**返回：** 支付跳转链接（`mweb_url`）

---

#### `queryOrder(String orderNo)`
查询订单状态。

**参数：**
- `orderNo` - 商户订单号

**返回：** `WxPayOrderQueryResult` 订单查询结果

---

#### `closeOrder(String orderNo)`
关闭未支付订单。

**参数：**
- `orderNo` - 商户订单号

**返回：** `WxPayOrderCloseResult` 关闭结果

---

#### `refund(String orderNo, String refundNo, int totalAmount, int refundAmount, String reason)`
申请退款。

**参数：**
- `orderNo` - 原支付订单号
- `refundNo` - 商户退款单号
- `totalAmount` - 订单总金额（分）
- `refundAmount` - 退款金额（分）
- `reason` - 退款原因

**返回：** `WxPayRefundResult` 退款结果

---

#### `queryRefund(String refundNo)`
查询退款状态。

**参数：**
- `refundNo` - 商户退款单号

**返回：** `WxPayRefundQueryResult` 退款查询结果

---

#### `parsePayNotify(String xmlData)`
解析支付回调通知。

**参数：**
- `xmlData` - 微信发送的回调 XML 数据

**返回：** `WxPayOrderNotifyResult` 解析结果

---

#### `parseRefundNotify(String xmlData)`
解析退款回调通知。

**参数：**
- `xmlData` - 微信发送的回调 XML 数据

**返回：** `WxPayRefundNotifyResult` 解析结果

---

#### `buildNotifySuccessResponse()`
生成回调成功响应。

**返回：** XML 格式成功响应字符串

---

#### `buildNotifyFailResponse(String msg)`
生成回调失败响应。

**参数：**
- `msg` - 错误消息

**返回：** XML 格式失败响应字符串

---

## 注意事项

### 1. 金额单位

微信支付接口中，**金额单位为分**，调用方法前需要将元转换为分：

```java
// 错误：直接传元
wechatPayUtil.createNativeOrder(orderNo, 100, "商品");  // 实际只收1元

// 正确：元转分
wechatPayUtil.createNativeOrder(orderNo, 100 * 100, "商品");  // 收100元
```

### 2. 订单号规则

- 商户订单号（`out_trade_no`）建议格式：时间戳 + 随机数 + 业务标识
- 长度不超过 32 个字符
- 同一商户号下订单号必须唯一

```java
// 推荐订单号生成方式
String orderNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        + String.format("%04d", (int)(Math.random() * 10000))
        + "PAY";
// 示例：202603121030451234PAY
```

### 3. 回调地址

- 必须是外网可访问的 HTTPS 地址
- 不能携带自定义参数（如 `?userId=123`）
- 需要正确返回 XML 响应，否则微信会认为通知失败并重发

### 4. 证书配置

- 商户私钥文件（`apiclient_key.pem`）需要妥善保管
- 生产环境建议将私钥内容配置在环境变量或配置中心，而非文件路径
- 沙箱环境使用测试证书，生产环境使用正式证书

### 5. 幂等性处理

支付回调可能会被多次发送，需要做好幂等性处理：

```java
@PostMapping("/callback")
public String payCallback(@RequestBody String xmlData) {
    WxPayOrderNotifyResult result = wechatPayUtil.parsePayNotify(xmlData);
    String orderNo = result.getOutTradeNo();
    
    // 先查询订单状态，已处理的直接返回成功
    Order order = orderService.getByOrderNo(orderNo);
    if (order.getStatus() == OrderStatus.PAID) {
        return wechatPayUtil.buildNotifySuccessResponse();
    }
    
    // 处理支付成功逻辑
    // ...
    
    return wechatPayUtil.buildNotifySuccessResponse();
}
```

### 6. 异常处理

工具类中的方法在遇到错误时会抛出 `RuntimeException`，建议统一异常处理：

```java
@ExceptionHandler(RuntimeException.class)
public Result<Void> handleException(RuntimeException e) {
    if (e.getMessage().contains("支付")) {
        return Result.error("支付操作失败: " + e.getMessage());
    }
    return Result.error(e.getMessage());
}
```

### 7. 沙箱环境

开发测试时可启用沙箱环境：

```yaml
wechat:
  pay:
    sandbox: true
```

沙箱环境地址：https://api.mch.weixin.qq.com/sandboxnew/

### 8. 安全建议

- 不要在客户端暴露商户密钥
- 回调接口需要验证签名（工具类已自动处理）
- 敏感操作（如退款）需要增加额外的权限校验

---

## 常见问题

### Q: 如何获取用户的 OpenID？

A: 微信公众号/小程序需要通过授权登录获取用户 OpenID：

```java
// 1. 引导用户访问授权链接
String authUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
    "appid=" + appId +
    "&redirect_uri=" + URLEncoder.encode(callbackUrl, "UTF-8") +
    "&response_type=code" +
    "&scope=snsapi_base" +  // 静默授权
    "&state=STATE#wechat_redirect";

// 2. 用户授权后，通过 code 获取 OpenID
String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
    "appid=" + appId +
    "&secret=" + secret +
    "&code=" + code +
    "&grant_type=authorization_code";
// 返回结果中包含 openid
```

### Q: 支付成功后订单状态没有更新？

A: 检查以下几点：
1. 回调地址是否外网可访问
2. 回调接口是否正确返回 XML 响应
3. 服务器防火墙是否放行微信支付 IP 段
4. 查看日志确认是否收到回调通知

### Q: 如何测试支付功能？

A: 有以下几种方式：
1. **沙箱环境**：使用微信提供的测试商户号和金额（如 1.01元）
2. **扫码支付**：使用微信扫描生成的二维码，支付 0.01 元测试
3. **模拟回调**：调用回调接口模拟微信支付通知

---

## 相关链接

- [微信支付开发文档](https://pay.weixin.qq.com/wiki/doc/api/index.html)
- [weixin-java-pay GitHub](https://github.com/binarywang/weixin-java-pay)
- [微信支付商户平台](https://pay.weixin.qq.com/)

---

**文档版本：** 1.0  
**最后更新：** 2026-03-12
