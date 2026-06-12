package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 获取指定群组群成员DTO
 * @date 2026/6/10 下午4:04
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetGroupMemberDTO {
    private String groupID;
    private String keyword;
    private pagination pagination;
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class pagination{
        public int pageNumber;
        public int showNumber;
    }
}
