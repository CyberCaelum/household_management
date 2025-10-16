package org.cybercaelum.household_management.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 简历类
 * @date 2025/10/15 下午8:19
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Resume implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id; //主键
    private Long userId; //用户主键
    private String resumeData; //简历信息
    private LocalDateTime createDate; //创建时间
    private LocalDateTime updateDate; //修改时间
    private Integer visibility; //是否可见，0为不可见，1为可见
}
