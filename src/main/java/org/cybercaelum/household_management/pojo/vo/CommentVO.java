package org.cybercaelum.household_management.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 评论返回VO
 * @date 2026/2/18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentVO implements Serializable {
    private Long id;//主键
    private String content;//内容
    private LocalDateTime createTime;//发布时间
    private Integer status;//状态，0为被删除，1可见
    private Long userId;//用户Id（评论者）
    private String username;//评论者用户名
    private String profileUrl;//评论者头像
    private Long commentedUserId;//被评论的用户Id
    private String commentedUsername;//被评论者用户名
    private Integer commentLevel;//评论分数，1-5分
    private Long orderId;//订单id
}
