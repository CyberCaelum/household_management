package org.cybercaelum.household_management.service;

import org.cybercaelum.household_management.pojo.dto.CsGroupAssignmentResult;
import org.cybercaelum.household_management.pojo.dto.MessageCallbackDTO;
import org.cybercaelum.household_management.pojo.dto.OpenimUserCallbackDTO;
import org.cybercaelum.household_management.pojo.dto.OpenimCallbackDTO;
import org.cybercaelum.household_management.pojo.dto.SessionEndDTO;

public interface CustomerServiceService {
    OpenimCallbackDTO afterOnline(OpenimUserCallbackDTO userCallbackDTO);

    OpenimCallbackDTO afterOffLine(OpenimUserCallbackDTO userCallbackDTO);

    OpenimCallbackDTO freshCsGroup(MessageCallbackDTO messageCallbackDTO);
    
    /**
     * @description 分配客服给用户
     * @author CyberCaelum
     * @date 2026/3/31
     * @param userId 用户id
     * @return CsGroupAssignmentResult 分配结果
     **/
    CsGroupAssignmentResult createCsGroup(Long userId);

    /**
     * @description 将用户添加到等待队列
     * @author CyberCaelum
     * @date 2026/3/31
     * @param userId 用户id
     **/
    void addToWaitingQueue(Long userId);
    
    /**
     * @description 获取用户在等待队列中的位置
     * @author CyberCaelum
     * @date 2026/3/31
     * @param userId 用户id
     * @return 位置（从0开始），如果不在队列中返回-1
     **/
    int getWaitingPosition(Long userId);
    
    /**
     * @description 主动结束会话
     * @author CyberCaelum
     * @date 2026/3/31
     * @param sessionEndDTO 结束会话信息
     * @return 是否成功
     **/
    boolean endSession(SessionEndDTO sessionEndDTO);
    
    /**
     * @description 获取用户的当前会话信息
     * @author CyberCaelum
     * @date 2026/3/31
     * @param userId 用户id
     * @return 会话信息map，如果没有会话返回null
     **/
    java.util.Map<String, String> getUserSession(Long userId);
    
    /**
     * @description 处理会话超时
     * @author CyberCaelum
     * @date 2026/3/31
     * @param userId 用户id
     **/
    void handleSessionTimeout(Long userId);
    
    /**
     * @description 客服离线时结束其所有会话
     * @author CyberCaelum
     * @date 2026/3/31
     * @param csId 客服id
     **/
    void endAllSessionsByCs(Long csId);

    CsGroupAssignmentResult requestCustomerService(Long userId);

    Integer getPosition(Long userId);

    void toEndSession(SessionEndDTO sessionEndDTO);

    void releaseCsSession(Long csId, Long userId);
}
