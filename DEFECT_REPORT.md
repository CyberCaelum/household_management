# 家政服务交易平台缺陷报告

> 生成日期：2026-04-16  
> 更新日期：2026-04-18  
> 分析范围：`src/main/java`、`src/main/resources`、`src/test`、`pom.xml`

---

## 一、项目概况

| 项目属性 | 说明 |
|---------|------|
| 项目名称 | Household Management（家政服务交易平台） |
| 技术栈 | Spring Boot 3.5.6 + Java 17 + MyBatis + MariaDB + Redis + RocketMQ |
| 主代码量 | 203 个 Java 文件 |
| 测试覆盖 | 仅 1 个空壳 `@SpringBootTest` |
| 整体完成度 | **约 70% ~ 75%** |

### 核心业务模块状态
- **用户认证/注册登录**：✅ 已完成
- **家政招募/简历投递**：✅ 已完成
- **AI 智能客服（RAG）**：✅ 已基本完成
- **OpenIM 即时通讯**：🟡 基础可用，部分待完善
- **人工客服工作台**：🟡 可用，但定时任务失效
- **订单管理**：🟡 流程框架已搭建
- **支付与资金结算**：🔴 大量 TODO，未形成闭环

---

## 二、缺陷严重等级说明

| 等级 | 说明 |
|------|------|
| 🔴 **P0 - 严重** | 导致系统无法启动、核心业务流程中断、资金风险或数据不一致 |
| 🟠 **P1 - 高** | 影响关键业务功能正常使用，有明显的功能缺失或安全隐患 |
| 🟡 **P2 - 中** | 影响非核心功能或体验，有替代方案或临时绕过方式 |
| 🟢 **P3 - 低** | 代码质量、注释、命名、文件位置等规范性问题 |

---

## 三、缺陷清单

### 🔴 P0 严重缺陷

#### P0-003：订单结算未真正实现资金打款给雇员
- **文件**：`src/main/java/org/cybercaelum/household_management/service/impl/OrderServiceImpl.java`
- **位置**：第 426 行、第 1194 行、第 1576 行
- **问题**：多处标记 `//TODO 给雇员打款`、`// TODO: 调用支付系统，将托管金额打给被雇人员`，但实际未调用微信企业付款或支付宝转账接口。
- **影响**：**核心交易闭环断裂**，订单完成后平台无法将资金结算给家政人员，项目不能用于真实交易。
- **建议**：接入微信支付「企业付款到零钱」或支付宝「单笔转账」接口，完成结算逻辑。

#### P0-004：管理员取消订单后未执行退款
- **文件**：`OrderServiceImpl.java`
- **位置**：第 745 行
- **问题**：`//TODO 判断是否付款然后进行退款处理` — 管理员取消订单后，仅更新了订单状态，未触发实际退款。
- **影响**：用户已支付的资金被悬空，造成资金占用和用户投诉风险。
- **建议**：根据支付渠道（微信/支付宝）调用对应的退款接口，并处理退款回调。

#### ~~P0-005：争议处理流程未闭环~~ ✅ **已修复（手动分配）**
- **修复内容**：
  - `respondCancelApplication` 拒绝分支：自动将状态改为 `PLATFORM_PROCESSING`，并创建 `DisputeResolution` 记录
  - `processTimeoutCancelApplications`：自动创建 `DisputeResolution` 记录
  - `employerDisputeDaily`：自动创建 `DisputeResolution` 记录
  - `getPendingDisputes`：改为查询 `DisputeResolution` 表，返回 `DisputeResolution.id`，与 `assignDispute` 对应
  - `platformDecideCancelApplication`：裁决后同步更新 `DisputeResolution` 记录
  - 新增 `platformDecideDailyDispute` 接口：支持每日确认争议的平台裁决
- **当前手动分配闭环**：
  1. 争议发生 → 自动创建 `DisputeResolution` 记录
  2. 管理员查询待处理列表 → `GET /kefu/disputes/pending`（返回 `DisputeResolution.id`）
  3. 管理员分配争议给客服 → `POST /kefu/disputes/assign/{disputeId}?kefuId=xxx`
  4. 客服处理/裁决 → `PUT /admin/platformDecide/{applicationId}` 或 `PUT /admin/platformDecideDaily/{confirmationId}`
  5. 执行退款 → `refund(orderId)` → 微信回调 `refundSuccess`

---

### 🟠 P1 高优先级缺陷

#### ~~P1-002：取消申请被驳回后未触发平台介入~~ ✅ **已修复**
- `respondCancelApplication` 拒绝分支已改为 `PLATFORM_PROCESSING` 状态，并自动创建争议记录。

#### P1-003：平台裁决取消时缺少违约方判定
- **文件**：`OrderServiceImpl.java`
- **位置**：第 1162 行
- **问题**：`//TODO 平台取消需要判断是哪一方违约` — 平台强制取消订单时未判定责任归属。
- **影响**：无法正确执行违约金扣除或信用分扣减。
- **建议**：根据订单履约记录和双方举证，判定违约方并执行相应处罚逻辑。

#### P1-004：退款异常时未通知管理员人工兜底
- **文件**：`OrderServiceImpl.java`
- **位置**：第 1662 行
- **问题**：`// TODO: 发送通知给管理员，人工处理` — 退款失败/异常时没有告警通知。
- **影响**：资金异常可能长时间无人发现，造成财务风险。
- **建议**：退款异常时发送企业微信/钉钉/邮件告警给运营或财务人员。

---

### 🟡 P2 中优先级缺陷

#### P2-001：创建客服群组时未加入机器人
- **文件**：`src/main/java/org/cybercaelum/household_management/service/impl/GroupServiceImpl.java`
- **位置**：第 168 行
- **问题**：`//TODO 需要增加机器人` — 创建客服群组时未将 AI 客服机器人加入群聊。
- **影响**：机器人无法自动回复群内消息，增加人工客服压力。
- **建议**：在创建群组时调用 OpenIM 接口将机器人账号加入群组。

#### P2-003：支付回调地址依赖环境变量，本地调试困难
- **文件**：`src/main/resources/application.yml`
- **问题**：`wechat.pay.notify-url` 等配置无默认值，完全依赖环境变量注入。
- **影响**：本地无法完整调试支付回调链路。
- **建议**：在 `application-dev.yml` 中配置本地调试用的回调地址（如内网穿透地址或占位符）。

#### P2-004：无数据库初始化脚本
- **文件**：缺失 `schema.sql` / `data.sql`
- **问题**：新环境中没有表结构初始化脚本，项目无法快速部署。
- **影响**：增加新成员/新环境接入成本。
- **建议**：补充表结构初始化脚本和基础数据脚本（常量、角色等）。

---

### 🟢 P3 低优先级/规范性问题

#### P3-001：源码目录中混入了 Markdown 文档
- **文件**：`src/main/java/org/cybercaelum/household_management/utils/WechatPayUtil.md`
- **问题**：Markdown 文档被错误放置在 Java 源码目录下，项目实际使用的是 `WechatPayUtil.java`。
- **建议**：删除该文件或移动到 `docs/` 目录。

#### P3-002：OpenIM Feign 客户端方法命名疑似拼写错误
- **文件**：`src/main/java/org/cybercaelum/household_management/feign/OpenimFeignClient.java`
- **问题**：存在 `sendMag(...)` 方法，疑似应为 `sendMsg(...)`。
- **建议**：修正方法名及所有调用处。

#### P3-003：多处已实现但残留的 TODO 注释未清理
- **涉及文件**：
  - `AiChatServiceImpl.java` 第 28 行：`//TODO 存入向量数据库`（已实现）
- **建议**：清理过时的 TODO 注释，避免误导后续维护人员。

#### P3-004：测试覆盖率几乎为零
- **文件**：`src/test/java/.../HouseholdManagementApplicationTests.java`
- **问题**：只有一个空的上下文加载测试，无任何单元测试或集成测试。
- **建议**：为核心 Service（尤其是订单、支付相关）补充单元测试和集成测试。

#### P3-005：`OrderServiceImpl` 代码量过大，职责过重
- **文件**：`OrderServiceImpl.java`
- **问题**：单文件长达 **1820 行**，包含了下单、取消、争议、结算、退款等多个职责。
- **建议**：按业务子域拆分为 `OrderCreateService`、`OrderCancelService`、`OrderSettlementService`、`OrderDisputeService` 等。

---

## 四、改进建议汇总

### 短期（1 ~ 2 周）
1. **清理规范性问题**：删除 `WechatPayUtil.md`、修正 `sendMag` 拼写、清理 `AiChatServiceImpl` 中已实现的 TODO。
2. **完善争议处理**：`GroupController` 暴露创建争议群组的 REST 接口（当前第 70 行只有注释）。

### 中期（2 ~ 4 周）
3. **完成支付闭环**：
   - 订单结算时调用微信/支付宝转账接口给雇员打款；
   - 管理员取消订单时触发退款；
   - 退款异常时增加告警通知。
4. **平台裁决违约方判定**：在 `platformDecideCancelApplication` 和 `platformDecideDailyDispute` 中补充违约方判定逻辑。

### 长期（1 ~ 2 月）
5. **代码重构**：将 `OrderServiceImpl` 按职责拆分为多个 Service。
6. **补全测试**：为核心业务编写单元测试和集成测试，提升代码稳定性。
7. **补充数据库脚本**：提供 `schema.sql` 和基础数据脚本，降低部署成本。

---

## 五、附录：关键文件速查

| 文件路径 | 备注 |
|---------|------|
| `src/main/java/.../service/impl/OrderServiceImpl.java` | 核心交易 TODO 集中地（P0-003~004、P1-003~004） |
| `src/main/java/.../service/impl/CustomerServiceServiceImpl.java` | 客服分配、争议查询、群组管理 |
| `src/main/java/.../controller/customer_service/CustomerServiceController.java` | 争议手动分配接口 |
| `src/main/java/.../controller/admin/AdminOrderController.java` | 平台裁决接口（取消申请 + 每日确认争议） |
| `src/main/java/.../controller/user/GroupController.java` | 争议群组接口未暴露（P2-001 相关） |
| `src/main/java/.../service/impl/GroupServiceImpl.java` | 未加入机器人（P2-001） |
| `src/main/java/.../feign/OpenimFeignClient.java` | `sendMag` 拼写错误（P3-002） |
| `src/main/java/.../utils/WechatPayUtil.md` | 文件位置错误（P3-001） |
