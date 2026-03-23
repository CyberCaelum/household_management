package org.cybercaelum.household_management.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 系统机器号
 * @date 2026/3/23
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OpenimBoot {
    /**
     * 主键
     */
    private Long id;
    /**
     * 通知号昵称
     */
    private String nickName;
    /**
     * 通知号 ID
     */
    private String bootId;
    /**
     * 通知号头像
     */
    @Builder.Default
    private String faceUrl = "lalela";
    /**
     * 3或4
     */
    @Builder.Default
    private int appMangerLevel = 3;
}
