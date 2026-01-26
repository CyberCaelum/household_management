package org.cybercaelum.household_management.pojo.vo;

import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 简历返回信息
 * @date 2026/1/26 下午7:08
 */
public class ResumeVO {
    private String resumeData; //简历信息，markdown
    private LocalDateTime createDate; //创建时间
    private LocalDateTime updateDate; //修改时间
    private Integer visibility; //是否可见，0为不可见，1为可见
}
