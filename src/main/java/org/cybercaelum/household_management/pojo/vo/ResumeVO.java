package org.cybercaelum.household_management.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 简历返回信息
 * @date 2026/1/26 下午7:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeVO {
    private Long id; //主键
    private Long userId; //用户主键
    private String resumeData; //简历信息，markdown
    private LocalDateTime createDate; //创建时间
    private LocalDateTime updateDate; //修改时间
    private Integer visibility; //是否可见，0为不可见，1为可见
    private List<String> picture; //相关图片
}
