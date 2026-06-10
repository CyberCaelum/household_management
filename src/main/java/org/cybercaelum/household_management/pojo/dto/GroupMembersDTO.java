package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description:
 * @date 2026/6/10 下午4:10
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GroupMembersDTO {
    private Integer total;
    private List<Member> members;
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class Member {
        private String groupID;
        private String userID;
        private Integer roleLevel;
        private Long joinTime;
        private String nickname;
        private String faceURL;
        private Integer appMangerLevel;
        private Integer joinSource;
        private String operatorUserID;
        private String ex;
        private Long muteEndTime;
        private String inviterUserID;
    }
}
