package org.cybercaelum.household_management.feign;

import org.cybercaelum.household_management.pojo.dto.*;
import org.cybercaelum.household_management.pojo.entity.OpenimBoot;
import org.cybercaelum.household_management.pojo.entity.OpenimResult;
import org.cybercaelum.household_management.pojo.vo.GroupInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(
        name = "openim-client",
        url = "${household.openim.api-address}"
)
public interface OpenimFeignClient {

    /**
     * @description 请求openim增加聊天机器人
     * @author CyberCaelum
     * @date 2026/3/23
     * @param operationId 用于全局链路追踪，建议使用时间戳，在每个请求中独立
     * @param token 管理员token
     * @param request 机器人信息
     * @return org.cybercaelum.household_management.pojo.entity.OpenimResult<org.cybercaelum.household_management.pojo.dto.NotificationAccountInfo>
     **/
    @PostMapping("/user/add_notification_account")
    OpenimResult<NotificationAccountInfo> addNotificationAccount(
            @RequestHeader("operationID") String operationId,
            @RequestHeader("token") String token,
            @RequestBody OpenimBoot request
    );

    /**
     * @description 创建聊天群组
     * @author CyberCaelum
     * @date 上午8:48 2026/3/25
     * @param operationId 用于全局链路追踪，建议使用时间戳，在每个请求中独立
     * @param token 管理员token
     * @param request 机器人信息
     * @return org.cybercaelum.household_management.pojo.entity.OpenimResult<org.cybercaelum.household_management.pojo.dto.NotificationAccountInfo>
     **/
    @PostMapping("/group/create_group")
    OpenimResult<GroupInfo> createGroup(
            @RequestHeader("operationID") String operationId,
            @RequestHeader("token") String token,
            @RequestBody OpenimGroupCreateDTO request
    );

    /**
     * @description 查询用户在线状态
     * @author CyberCaelum
     * @date 2026/3/29
     * @param operationId 用于全局链路追踪，建议使用时间戳，在每个请求中独立
     * @param token 管理员token
     * @param request 用户id列表
     * @return org.cybercaelum.household_management.pojo.entity.OpenimResult
     **/
    @PostMapping("/user/get_users_online_status")
    OpenimResult<List<UserOnlineStatusDTO>> getUsersOnlineStatus(
            @RequestHeader("operationID") String operationId,
            @RequestHeader("token") String token,
            @RequestBody GetUsersOnlineStatusDTO request
    );

    /**
     * @description 添加openim账号好友
     * @author CyberCaelum
     * @date 2026/3/30
     * @param operationId 用于全局链路追踪，建议使用时间戳，在每个请求中独立
     * @param token 管理员token
     * @param request 用户id列表
     * @return org.cybercaelum.household_management.pojo.entity.OpenimResult<java.lang.String>
     **/
    @PostMapping("/friend/import_friend")
    OpenimResult<String> importFriend(
            @RequestHeader("operationID") String operationId,
            @RequestHeader("token") String token,
            @RequestBody ImportFriendDTO request
    );

    OpenimResult<GroupListDTO> getGroupsInfo(
            @RequestHeader("operationID") String operationId,
            @RequestHeader("token") String token,
            @RequestBody List<String> request
    );
}
