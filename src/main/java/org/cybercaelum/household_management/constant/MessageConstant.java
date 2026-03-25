package org.cybercaelum.household_management.constant;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 信息提示异常类
 * @date 2025/10/20 下午8:04
 */
public class MessageConstant {
    public static final String PASSWORD_ERROR = "密码错误";
    public static final String ACCOUNT_NOT_FOUND = "账号不存在";
    public static final String ACCOUNT_LOCKED = "账号被锁定";
    public static final String ACCOUNT_EXISTS = "已存在";
    public static final String UNKNOWN_ERROR = "未知错误";
    public static final String USER_NOT_LOGIN = "用户未登录";
    public static final String LOGIN_FAILED = "登录失败";
    public static final String UPLOAD_FAILED = "文件上传失败";
    public static final String PHONE_NUMBER_USED = "电话号已使用";
    public static final String USERNAME_EXIST = "用户名已存在";
    public static final String TITLE_IS_EMPTY = "标题不能为空";
    public static final String TITLE_TOO_LONG = "标题不能超过30字";
    public static final String MIN_SALARY_MINIMUM = "最小薪资不能低于0";
    public static final String MAX_SALARY_MAXIMUM = "最大薪资不能超过100000元";
    public static final String SALARY_RANGE_ERROR = "薪资范围不合理";
    public static final String TIME_RANGE_ERROR = "时间范围不合理";
    public static final String REQUIREMENT_TOO_LONG = "需求不能超过1000字";
    public static final String STATUS_ERROR = "状态错误";
    public static final String ADDRESS_ERROR = "地址信息错误";
    public static final String USERNAME_EMPTY = "用户名不能为空";
    public static final String USERNAME_TOO_LONG = "用户名不能超过8个字符";
    public static final String USERNAME_STANDARD = "用户名只能包含中文、字母和数字";
    public static final String PASSWORD_EMPTY = "密码不能为空";
    public static final String PASSWORD_LENGTH_RANGE = "密码长度必须在6-18位之间";
    public static final String PASSWORD_STANDARD = "密码必须包含数字、小写字母、大写字母和特殊符号";
    public static final String PHONE_EMPTY = "电话号码不能为空";
    public static final String PHONE_LENGTH_STANDARD = "电话号码必须是11位";
    public static final String PHONE_STANDARD = "电话号码格式不正确";
    public static final String MIN_SALARY_IS_NULL = "最低薪资不能为空";
    public static final String MAX_SALARY_IS_NULL = "最高薪资不能为空";
    public static final String DURATION_TYPE_ERROR = "天数范围错误";
    public static final String ROLE_TYPE_ERROR = "用户种类错误";
    public static final String USERID_IS_NULL = "用户id为空";

    // 评论相关
    public static final String COMMENT_CONTENT_EMPTY = "评论内容不能为空";
    public static final String COMMENT_CONTENT_TOO_LONG = "评论内容不能超过500字";
    public static final String COMMENT_LEVEL_EMPTY = "评分不能为空";
    public static final String COMMENT_LEVEL_INVALID = "评分必须在1-5分之间";
    public static final String COMMENTED_USER_ID_EMPTY = "被评论用户不能为空";
    public static final String ORDER_ID_EMPTY = "订单不能为空";
    public static final String COMMENT_NOT_FOUND = "评论不存在";
    public static final String COMMENT_NOT_ALLOWED = "您无权操作此评论";
    public static final String COMMENT_ALREADY_EXISTS = "您已对该订单发表过评价";

    // 订单相关
    public static final String RECRUITMENT_ID_EMPTY = "招募信息不能为空";
    public static final String PRICE_EMPTY = "价格不能为空";
    public static final String PRICE_MIN = "价格不能低于0";
    public static final String START_TIME_EMPTY = "开始时间不能为空";
    public static final String END_TIME_EMPTY = "结束时间不能为空";
    public static final String DAYS_RANGE_ERROR = "工作天数必须在1-100天之间";
    public static final String ORDER_TIME_RANGE_ERROR = "时间范围不合理";
    public static final String ORDER_STATUS_ERROR = "订单状态错误";
    public static final String GROUP_TYPE_ERROR = "群组类型错误";
}
