package org.cybercaelum.household_management.service;

import org.cybercaelum.household_management.pojo.dto.SessionCreateDTO;
import org.cybercaelum.household_management.pojo.vo.SessionCreateVO;

import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 会话服务接口
 * @date 2026/3/1
 */
public interface SessionService {

    /**
     * @description 创建或获取会话（用户点击私聊时调用）
     * @author CyberCaelum
     * @date 2026/3/1
     * @param sessionCreateDTO 创建会话请求
     * @return org.cybercaelum.household_management.pojo.vo.SessionCreateVO
     **/
    SessionCreateVO createOrGetSession(SessionCreateDTO sessionCreateDTO);

    /**
     * @description 获取当前用户的会话列表
     * @author CyberCaelum
     * @date 2026/3/1
     * @return java.util.List<org.cybercaelum.household_management.pojo.vo.SessionCreateVO>
     **/
    List<SessionCreateVO> getUserSessions();

    /**
     * @description 关闭会话
     * @author CyberCaelum
     * @date 2026/3/1
     * @param sessionId 会话id
     **/
    void closeSession(Long sessionId);
}
