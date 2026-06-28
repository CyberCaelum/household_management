# Household Management — 家政服务交易平台

> 一个基于 Spring Boot 的家政服务在线交易平台，支持用户认证、家政招募与简历投递、订单管理、即时通讯、AI 智能客服等功能。

---

## 技术栈

| 技术 | 版本/说明 |
|------|----------|
| Java | 17 |
| Spring Boot | 3.5.6 |
| MyBatis | 持久层框架 |
| MariaDB | 关系型数据库 |
| Redis | 缓存与分布式锁 |
| RocketMQ | 消息队列 |
| OpenIM | 即时通讯 |
| Spring AI + Alibaba Cloud AI | AI 智能客服（RAG） |
| 支付宝 SDK | alipay-sdk-java 4.38.10.ALL |
| 微信支付 SDK | weixin-java-pay 4.6.0 |

---

## 项目结构

```
src/main/java/org/cybercaelum/household_management/
├── ai/            # AI 智能客服（RAG 检索增强生成）
├── annotation/    # 自定义注解
├── aspect/        # AOP 切面
├── config/        # 配置类
├── constant/      # 常量定义
├── consumer/      # RocketMQ 消息消费者
├── context/       # 上下文工具（如用户信息）
├── controller/    # REST 控制器
├── enumeration/   # 枚举类
├── exception/     # 异常处理
├── feign/         # Feign 远程调用客户端（OpenIM等）
├── handler/       # 处理器
├── interceptor/   # 拦截器
├── listener/      # 事件监听器
├── mapper/        # MyBatis Mapper 接口
├── pojo/          # 实体类、DTO、VO
├── properties/    # 配置属性类
├── service/       # 业务逻辑层
├── task/          # 定时任务
└── utils/         # 工具类（支付、文件等）
docs/              # 项目文档
```

---

## 核心模块完成状态

| 模块 | 状态 | 说明 |
|------|------|------|
| 用户认证/注册登录 | 已完成 | 支持登录、注册、Token 刷新 |
| 家政招募/简历投递 | 已完成 | 发布招募、投递简历 |
| AI 智能客服（RAG） | 已完成 | 基于 Spring AI 的检索增强生成问答 |
| OpenIM 即时通讯 | 基础可用 | 基础可用，部分功能待完善 |
| 人工客服工作台 | 基本可用 | 会话超时清理依赖 Redis 事件通知，缺少兜底机制 |
| 订单管理 | 框架已搭建 | 下单/取消/争议/结算流程骨架完整，但关键资金操作仍为 TODO |
| **支付与资金结算** | 待完善 | **关键闭环缺失，见下方说明** |

---

## 已知待完善

### 支付体系未形成闭环

当前项目中微信支付（`WechatPayUtil`）和支付宝支付（`AliPayUtil`）的工具类已完成封装，但**资金的实际流转尚未打通**，主要包括：

#### 1. 订单完成后未真正给雇员打款

- **位置**：`OrderServiceImpl.java`（多处 `//TODO 给雇员打款`）
- **问题**：订单完成后，平台侧仅更新了订单状态，并未调用微信「企业付款到零钱」或支付宝「单笔转账」接口将托管资金打款给家政人员。
- **影响**：**核心交易闭环断裂** — 用户已支付、服务已完成，但雇员收不到钱，无法用于真实业务。

#### 2. 管理员取消订单后未执行退款

- **位置**：`OrderServiceImpl.java`（`//TODO 判断是否付款然后进行退款处理`）
- **问题**：管理员取消已支付订单后，仅更新了订单状态，未调用微信/支付宝退款接口将款项退回给用户。
- **影响**：用户已支付的资金被悬空，存在资金占用和投诉风险。

#### 3. 退款异常缺少人工兜底告警

- **位置**：`OrderServiceImpl.java`（`//TODO 发送通知给管理员，人工处理`）
- **问题**：退款失败或异常时没有管理员告警机制，资金异常可能长时间无人发现。

#### 4. 订单结算/退款链路存在多处 TODO（与支付体系联动）

订单管理模块的骨架已完整（下单 → 支付回调 → 服务中 → 每日确认 → 结算），但 `OrderServiceImpl.java`（约 1820 行）中仍残留 6 处关键 TODO：

| 位置 | TODO 内容 | 影响 |
|------|----------|------|
| L431 | `//TODO 给雇员打款` | 独立的方法占位，未实现 |
| L750 | `//TODO 判断是否付款然后进行退款处理` | 管理员取消订单后仅改状态，不退款 |
| L1324 | `// TODO: 调用支付系统，将托管金额打给被雇人员` | 结算记录已入库但钱没转出去 |
| L1330 | `//TODO 更新订单信息` | 结算完成后未更新订单状态 |
| L1737 | `// 8. TODO: 给雇员打款` | 退款成功后未将雇员应得部分转账 |
| L1823 | `// TODO: 发送通知给管理员，人工处理` | 退款异常时无告警，资金风险无人发现 |

这些 TODO 正是上面支付体系问题的具体体现 — 订单流程能走到"创建结算记录"这一步，但从"记录"到"钱到账"的最后一步全部缺失。

#### 5. 支付回调地址未配置本地调试默认值

- **位置**：`application.yml`
- **问题**：`wechat.pay.notify-url` 等配置依赖环境变量注入，无默认值，本地无法完整调试支付回调链路。

---

### 🟡 客服会话超时清理机制存在单点风险

客服会话的超时清理依赖 **Redis 键过期事件通知**（`RedisKeyExpiredListener`），而非传统的 `@Scheduled` 定时任务。流程如下：

```
用户发消息 → freshCsGroup() 刷新 Redis Key TTL（20分钟）
     ↓ 20分钟无活动
Redis Key 过期 → Redis 发送 __keyevent@*__:expired 事件
     ↓
RedisKeyExpiredListener.onMessage() → releaseCsSession()
```

**问题在于：**

1. **Redis 服务端配置依赖**：Redis 必须配置 `notify-keyspace-events Ex`（键过期事件通知），如果部署时漏配，过期事件将不会触发，导致客服会话永久残留、客服容量无法释放。

2. **无定时兜底机制**：没有任何 `@Scheduled` 定时任务作为备用扫描方案。一旦 Redis 通知机制失效，所有超时会话都无法清理。

3. **存在死代码**：`CustomerServiceServiceImpl.handleSessionTimeout()` 方法已定义但从未被调用，计划中的超时回调逻辑未接入。

**影响**：客服下线后会话容量不释放、后续用户无法分配到客服、等待队列永不清空。

---

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MariaDB / MySQL
- Redis
- RocketMQ

### 1. 克隆项目

```bash
git clone <repository-url>
cd household_management
```

### 2. 配置数据库

修改 `src/main/resources/application-dev.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/household_management
    username: your_username
    password: your_password
```

### 3. 配置 Redis

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

### 4. 配置支付（可选，开发阶段可跳过）

参考以下文档进行支付配置：
- [docs/WechatPayUtil.md](docs/WechatPayUtil.md) — 微信支付配置与使用说明
- [docs/AliPayUtil.md](docs/AliPayUtil.md) — 支付宝支付配置与使用说明

> **开发提示**：微信支付工具类支持 Mock 模式，设置 `wechat.pay.mock: true` 即可在不配置真实商户号的情况下进行开发调试。

### 5. 启动项目

```bash
mvn spring-boot:run
```

或使用 IDE 运行 `HouseholdManagementApplication` 主类。

---

## 相关文档

- [docs/WechatPayUtil.md](docs/WechatPayUtil.md) — 微信支付工具类使用说明
- [docs/AliPayUtil.md](docs/AliPayUtil.md) — 支付宝支付工具类使用说明

---

## 许可证

MIT License

Copyright (c) 2026 CyberCaelum

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
