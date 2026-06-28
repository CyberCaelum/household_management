-- ============================================
-- Household Management 数据库建表脚本
-- 数据库: MySQL
-- 作者: CyberCaelum
-- ============================================

CREATE DATABASE IF NOT EXISTS household_management
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE household_management;

-- -------------------------------------------
-- 用户表
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS user
(
    id           BIGINT AUTO_INCREMENT COMMENT '用户id' PRIMARY KEY,
    phone_number VARCHAR(11)   NOT NULL COMMENT '手机号',
    username     VARCHAR(32)   NOT NULL COMMENT '用户名',
    password     VARCHAR(32)   NOT NULL COMMENT '密码',
    create_time  DATETIME      NOT NULL COMMENT '创建时间',
    status       INT DEFAULT 1 NOT NULL COMMENT '账号状态，0为注销，1为启用',
    role         INT DEFAULT 1 NOT NULL COMMENT '角色，0管理员，1用户',
    profile_url  VARCHAR(128)  NULL COMMENT '头像地址'
) COMMENT '用户表';

-- -------------------------------------------
-- 招募信息表
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS recruitment
(
    id            BIGINT AUTO_INCREMENT COMMENT '招募id' PRIMARY KEY,
    title         VARCHAR(64)                   NOT NULL COMMENT '标题 不能超过30字',
    mine_salary   DECIMAL(20, 4) DEFAULT 0.0000 NOT NULL COMMENT '最低薪资',
    max_salary    DECIMAL(20, 4)                NOT NULL COMMENT '最大薪资',
    start_time    DATE                          NOT NULL COMMENT '开始时间',
    end_time      DATE                          NOT NULL COMMENT '结束时间',
    requirement   VARCHAR(2000)                 NULL COMMENT '要求',
    status        INT            DEFAULT 2      NOT NULL COMMENT '招募的状态，0删除，1发布，2隐私，3结束',
    province_code VARCHAR(12)                   NULL COMMENT '省份编码',
    province_name VARCHAR(32)                   NULL COMMENT '省份名称',
    city_code     VARCHAR(12)                   NULL COMMENT '城市编码',
    city_name     VARCHAR(32)                   NULL COMMENT '城市名称',
    district_code VARCHAR(12)                   NULL COMMENT '区县编码',
    district_name VARCHAR(32)                   NULL COMMENT '区县名称',
    detail        VARCHAR(200)                  NULL COMMENT '详细地址信息 具体到门牌号',
    user_id       BIGINT                        NOT NULL COMMENT '发布人id',
    create_time   DATETIME                      NULL COMMENT '创建时间',
    update_time   DATETIME                      NULL COMMENT '更新时间'
) COMMENT '招募信息表';

-- -------------------------------------------
-- 简历表
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS resume
(
    id          BIGINT AUTO_INCREMENT COMMENT '简历id' PRIMARY KEY,
    user_id     BIGINT        NOT NULL COMMENT '用户id',
    resume_data TEXT          NULL COMMENT '简历信息，markdown格式',
    create_time DATETIME      NOT NULL COMMENT '创建时间',
    update_time DATETIME      NOT NULL COMMENT '更新时间',
    visibility  INT DEFAULT 0 NOT NULL COMMENT '是否可见，0不可见，1可见'
) COMMENT '简历';

-- -------------------------------------------
-- 简介图片表
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS resume_picture
(
    id          BIGINT AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    url         VARCHAR(128) NOT NULL COMMENT '图片地址',
    resume_id   BIGINT       NOT NULL COMMENT '简介id',
    create_time DATETIME     NULL COMMENT '上传时间',
    user_id     BIGINT       NOT NULL COMMENT '上传用户id',
    status      INT          NULL COMMENT '状态，0为删除，1为使用',
    update_time DATETIME     NULL COMMENT '更新时间'
) COMMENT '简介图片表';

-- -------------------------------------------
-- 订单表
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS `order`
(
    id               BIGINT AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    price            DECIMAL            NOT NULL COMMENT '价格',
    order_time       DATETIME           NULL COMMENT '下订单时间',
    recruitment_id   BIGINT             NOT NULL COMMENT '招募id',
    status           INT                NOT NULL COMMENT '订单状态，0为取消，1为进行中，2为完成，3为待被雇佣者确认，4退款，5待支付，6已接单',
    start_time       DATETIME           NULL COMMENT '开始时间',
    end_time         DATETIME           NULL COMMENT '结束时间',
    employer_id      BIGINT             NOT NULL COMMENT '雇佣用户id',
    employee_id      BIGINT             NULL COMMENT '被雇佣用户id',
    province_code    VARCHAR(32)        NULL COMMENT '省份编号',
    province_name    VARCHAR(32)        NULL COMMENT '省份名称',
    city_code        VARCHAR(32)        NULL COMMENT '城市编号',
    city_name        VARCHAR(32)        NULL COMMENT '城市名称',
    district_code    VARCHAR(32)        NULL COMMENT '区县编号',
    district_name    VARCHAR(32)        NULL COMMENT '区县名称',
    detail           VARCHAR(128)       NULL COMMENT '详细地址信息',
    total            DECIMAL(20, 4)     NULL COMMENT '总金额',
    days             INT                NULL COMMENT '工作总天数',
    order_number     VARCHAR(128)       NULL COMMENT '订单号',
    pay_method       INT                NULL COMMENT '支付方式，1微信，2支付宝',
    pay_status       INT                NULL COMMENT '支付状态，0未支付，1已支付，2已退款',
    cancel_reason    VARCHAR(256)       NULL COMMENT '订单取消原因',
    rejection_reason VARCHAR(256)       NULL COMMENT '订单拒绝原因',
    cancel_time      DATETIME           NULL COMMENT '订单取消时间',
    payment_time     DATETIME           NULL COMMENT '支付时间',
    rejection_time   DATETIME           NULL COMMENT '订单拒绝时间',
    refund_time      DATETIME           NULL COMMENT '退款时间',
    cancel_type      INT                NULL COMMENT '取消类型，0未取消，1协商一致取消，2雇主强制取消，3家政人员强制取消，4平台取消',
    held_amount      DECIMAL(20, 4)     NULL COMMENT '托管金额',
    refund_number    VARCHAR(32)        NULL COMMENT '商户退款单号'
) COMMENT '订单';

-- -------------------------------------------
-- 取消申请表
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS cancel_application
(
    id                BIGINT AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    order_id          BIGINT       NOT NULL COMMENT '关联订单ID',
    applicant_id      BIGINT       NULL COMMENT '申请人ID',
    applicant_role    INT          NULL COMMENT '申请人角色：1-雇主，2-家政人员',
    cancel_type       INT          NULL COMMENT '申请的取消类型：1-协商一致，2-雇主强制，3-家政人员强制',
    reason            VARCHAR(512) NULL COMMENT '申请理由',
    status            INT          NULL COMMENT '申请状态：1-待对方确认，2-对方已同意，3-对方已拒绝，4-平台介入处理中，5-平台已裁决',
    confirm_user_id   BIGINT       NULL COMMENT '确认方用户ID（当对方同意/拒绝时记录）',
    confirm_time      DATETIME     NULL COMMENT '确认时间',
    platform_decision INT          NULL COMMENT '平台裁决结果：1-同意取消，2-拒绝取消，3-部分结算等',
    platform_operator BIGINT       NULL COMMENT '平台操作人',
    platform_note     VARCHAR(512) NULL COMMENT '平台备注',
    expire_time       DATETIME     NULL COMMENT '超时时间（如24小时内未确认则自动转平台介入）',
    create_time       DATETIME     NULL COMMENT '创建时间',
    update_time       DATETIME     NULL COMMENT '更新时间'
) COMMENT '取消申请表';

-- -------------------------------------------
-- 每日服务确认表
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS daily_confirmation
(
    id                    BIGINT AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    order_id              BIGINT       NOT NULL COMMENT '订单id',
    service_date          DATE         NULL COMMENT '服务日期',
    status                INT          NULL COMMENT '确认状态：0待确认，1雇主已确认，2雇主拒绝/争议，3系统自动确认',
    worker_confirm_time   DATETIME     NULL COMMENT '家政人员发起确认的时间',
    employer_confirm_time DATETIME     NULL COMMENT '雇主确认的时间',
    auto_confirm_time     DATETIME     NULL COMMENT '系统自动确认的时间',
    dispute_reason        VARCHAR(512) NULL COMMENT '争议原因',
    create_time           DATETIME     NULL COMMENT '创建时间',
    update_time           DATETIME     NULL COMMENT '更新时间'
) COMMENT '每日服务确认表';

-- -------------------------------------------
-- 争议处理表
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS dispute_resolution
(
    id               BIGINT AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    order_id         BIGINT       NOT NULL COMMENT '订单id',
    source_type      INT          NULL COMMENT '争议来源，1-取消申请，2-每日确认，3-其他',
    source_id        BIGINT       NOT NULL COMMENT '来源记录ID（如cancel_application_id或daily_confirmation_id）',
    defaulting_party INT          NULL COMMENT '平台裁定的违约方：1-雇主，2-雇员',
    decision         INT          NULL COMMENT '裁决结果（如同意取消、部分结算等）',
    operator         BIGINT       NULL COMMENT '平台操作人',
    note             VARCHAR(512) NULL COMMENT '平台备注',
    create_time      DATETIME     NULL COMMENT '创建时间',
    kefu_id          BIGINT       NULL COMMENT '分配的客服ID'
) COMMENT '争议处理表';

-- -------------------------------------------
-- 评论表
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS comment
(
    id                BIGINT AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    content           VARCHAR(1024) NULL COMMENT '内容',
    create_time       DATETIME      NULL COMMENT '发布时间',
    status            INT           NOT NULL COMMENT '状态，0为被删除，1为可见',
    user_id           BIGINT        NOT NULL COMMENT '用户id',
    commented_user_id BIGINT        NOT NULL COMMENT '被评论用户id',
    comment_level     INT           NOT NULL COMMENT '评论分数1-5分',
    order_id          BIGINT        NOT NULL COMMENT '订单id',
    update_time       DATETIME      NULL COMMENT '修改时间'
) COMMENT '评论表';

-- -------------------------------------------
-- 结算记录表
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS settlement
(
    id                BIGINT AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    order_id          BIGINT         NOT NULL COMMENT '关联订单ID',
    total_days        INT            NULL COMMENT '总确认天数（取自daily_confirmation中有效的天数）',
    daily_rate        DECIMAL(10, 4) NULL COMMENT '日薪',
    total_amount      DECIMAL(10, 4) NULL COMMENT '应付总金额',
    penalty_deduction DECIMAL(10, 4) NULL COMMENT '违约金扣除',
    final_amount      DECIMAL(10, 4) NULL COMMENT '最终支付金额',
    status            INT            NULL COMMENT '结算状态：0-待结算，1-已结算，2-结算异常',
    settlement_time   DATETIME       NULL COMMENT '结算时间',
    create_time       DATETIME       NULL COMMENT '创建时间',
    defaulting_party  INT            NULL COMMENT '违约方，1-雇主违约，2-雇员违约',
    order_number      VARCHAR(32)    NULL COMMENT '订单号',
    refund_number     VARCHAR(32)    NULL COMMENT '退款订单号'
) COMMENT '结算记录表 ';
