package org.cybercaelum.household_management.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: openim回调返回类
 * @date 2026/3/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpenimCallbackVO {
    private Integer actionCode;//表示业务系统的回调是否正确执行。0表示操作成功。
    private Integer errCode;//表示自定义错误码，此处填0代表忽略回调结果。
    private String errMsg;//自定义错误码对应的简单错误信息。
    private String errDlt;//自定义错误码对应的详细错误信息。
    private Integer nextCode;//下一步执行指令，1表示拒绝继续执行，actionCode等于0时设置。
}
