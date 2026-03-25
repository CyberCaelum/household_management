package org.cybercaelum.household_management.pojo.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cybercaelum.household_management.constant.MessageConstant;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 创建群组
 * @date 2026/3/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupCreateDTO {
    //群组发起人id
    @NotNull(message = MessageConstant.USERID_IS_NULL)
    private Long initiator;
    //接受人id
    @NotNull(message = MessageConstant.USERID_IS_NULL)
    private Long accepter;
    //对应的招募id
    @NotNull(message = MessageConstant.RECRUITMENT_ID_EMPTY)
    private Long recruitmentId;
    //发起的是什么请求，客服介入还是私聊，1为私聊，2为客服
    private Integer groupType;
    @AssertTrue(message = MessageConstant.GROUP_TYPE_ERROR)
    private boolean isGroupTypeValid(){
        return groupType != null && (groupType ==1||groupType ==2);
    }
}
