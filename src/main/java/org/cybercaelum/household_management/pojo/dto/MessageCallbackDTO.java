package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 消息回调信息
 * @date 2026/3/30
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageCallbackDTO {
    private String sendID;//发送者的用户ID
    private String callbackCommand;//回调命令，这里是发送群聊消息后的回调
    private String serverMsgID;//服务器生成的消息ID
    private String clientMsgID;//客户端生成的消息ID
    private String operationID;//operationID用于全局链路追踪
    private Integer senderPlatformID;//发送者的平台ID
    private String senderNickname;//发送者的昵称
    private Integer sessionType;//会话类型,1：单聊，2：群聊，4：系统通知
    private Integer msgFrom;//消息来源，100来源于用户发送，200来源于管理员发送或者系统广播通知等
    private Integer contentType;//消息内容类型，101表示文本消息，102表示图片消息，103表示语音消息，...
    private Integer status;//消息状态，1表示发送成功，2表示发送失败
    private Long sendTime;//消息发送的时间戳（毫秒）
    private Long createTime;//消息创建的时间戳（毫秒）
    private String content;//消息内容
    private Long seq;//消息序号
    private List<String> atUserList;//群聊成员ID列表，单聊忽略
    private String faceURL;//发送者的头像URL
    private String ex;//额外的数据字段
    private String groupID;//接收者的用户ID
}
