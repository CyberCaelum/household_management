package org.cybercaelum.household_management.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cybercaelum.household_management.annotation.AutoFill;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 简历图片基础类
 * @date 2026/1/30 下午4:20
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResumePicture implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id; //主键
    private String url; //图片地址
    private Long resumeId;//简历id
    private LocalDateTime createTime; //创建时间
    private Long userId; //用户id
    private int status;//图片状态，0为删除，1为存在
    private LocalDateTime updateTime; //修改时间
}
