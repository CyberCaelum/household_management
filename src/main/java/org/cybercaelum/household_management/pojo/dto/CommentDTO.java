package org.cybercaelum.household_management.pojo.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cybercaelum.household_management.constant.MessageConstant;

import java.io.Serializable;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 评论DTO
 * @date 2026/2/18
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentDTO implements Serializable {

    private Long id;//主键，修改时必填

    @NotBlank(message = MessageConstant.COMMENT_CONTENT_EMPTY)
    @Size(max = 500, message = MessageConstant.COMMENT_CONTENT_TOO_LONG)
    private String content;//内容

    @NotNull(message = MessageConstant.COMMENT_LEVEL_EMPTY)
    @Min(value = 1, message = MessageConstant.COMMENT_LEVEL_INVALID)
    @Max(value = 5, message = MessageConstant.COMMENT_LEVEL_INVALID)
    private Integer commentLevel;//评论分数，1-5分

    @NotNull(message = MessageConstant.COMMENTED_USER_ID_EMPTY)
    private Long commentedUserId;//被评论的用户Id

    @NotNull(message = MessageConstant.ORDER_ID_EMPTY)
    private Long orderId;//订单id

    @AssertTrue(message = MessageConstant.COMMENT_LEVEL_INVALID)
    private boolean isCommentLevelValid() {
        return commentLevel == null || (commentLevel >= 1 && commentLevel <= 5);
    }
}
