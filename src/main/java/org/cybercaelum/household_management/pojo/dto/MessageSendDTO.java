package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: TODO
 * @date 2026/4/9 上午9:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageSendDTO {
    private String sendID;
    private String recvID;
    private Content content;
    private Integer contentType;
    private Integer sessionType;
    private boolean isOnlineOnly;
    private boolean notOfflinePush;
    private Long sendTime;
    private OfflinePushInfo offlinePushInfo;
    private String ex;
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        private String data;//用户自定义的消息内容
        private String description;//扩展描述
        private String extension;//扩展字段
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OfflinePushInfo {
        private String title;
        private String desc;
        private String ex;
        private String iOSPushSound;
        private boolean iOSBadgeCount;
    }
}
