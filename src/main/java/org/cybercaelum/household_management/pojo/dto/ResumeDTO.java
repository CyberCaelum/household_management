package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 简介DTO
 * @date 2026/1/23 下午4:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeDTO {
    private String resumeData; //简历信息，markdown
    private Integer visibility; //是否可见，0为不可见，1为可见
    private List<String> pictures; //简历附加的图片
}
