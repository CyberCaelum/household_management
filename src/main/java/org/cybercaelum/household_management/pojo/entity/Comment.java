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
 * @description: 评论实体类
 * @date 2026/2/18 下午9:10
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Comment implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;//主键
    private String content;//内容
    private LocalDateTime createTime;//发布时间
    private Integer status;//状态，0为被删除，1可见
    private Long userId;//用户Id（评论者）
    private Long commentedUserId;//被评论的用户Id
    private Integer commentLevel;//评论分数，1-5分
    private Long orderId;//订单id
    private LocalDateTime updateTime;//修改时间
}
