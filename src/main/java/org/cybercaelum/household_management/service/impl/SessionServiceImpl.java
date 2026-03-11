package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.context.BaseContext;
import org.cybercaelum.household_management.exception.BaseException;
import org.cybercaelum.household_management.mapper.RecruitmentMapper;
import org.cybercaelum.household_management.mapper.SessionMapper;
import org.cybercaelum.household_management.mapper.UserMapper;
import org.cybercaelum.household_management.pojo.dto.SessionCreateDTO;
import org.cybercaelum.household_management.pojo.entity.Recruitment;
import org.cybercaelum.household_management.pojo.entity.User;
import org.cybercaelum.household_management.pojo.entity.Session;
import org.cybercaelum.household_management.pojo.vo.RecruitmentVO;
import org.cybercaelum.household_management.pojo.vo.SessionCreateVO;
import org.cybercaelum.household_management.service.SessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 会话服务实现类
 * @date 2026/3/1
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionMapper sessionMapper;
    private final RecruitmentMapper recruitmentMapper;
    private final UserMapper userMapper;

    /**
     * @description 创建或获取会话
     * 模拟闲鱼模式：用户点击招募信息的"联系雇主"按钮后，创建或获取会话
     * @author CyberCaelum
     * @date 2026/3/1
     * @param sessionCreateDTO 创建会话请求
     * @return org.cybercaelum.household_management.pojo.vo.SessionCreateVO
     **/
    @Override
    @Transactional
    public SessionCreateVO createOrGetSession(SessionCreateDTO sessionCreateDTO) {
        Long currentUserId = BaseContext.getUserId();
        Long recruitmentId = sessionCreateDTO.getRecruitmentId();

        log.info("用户 {} 请求创建会话，招募ID: {}", currentUserId, recruitmentId);

        // 1. 查询招募信息
        RecruitmentVO recruitment = recruitmentMapper.selectRecruitmentUserInfoById(recruitmentId);
        if (recruitment == null) {
            throw new BaseException("招募信息不存在");
        }

        // 检查招募状态是否正常（只有发布状态才能发起私聊）
        if (recruitment.getStatus() != 1) {
            throw new BaseException("该招募已下架或已结束，无法发起私聊");
        }

        Long employerId = recruitment.getUserId();

        // 2. 不能自己联系自己
        if (Objects.equals(currentUserId, employerId)) {
            throw new BaseException("不能向自己发起私聊");
        }

        // 3. 当前用户作为雇员
        Long employeeId = currentUserId;

        // 4. 查询是否已存在活动会话
        Session existingSession = sessionMapper.selectActiveSession(recruitmentId, employeeId, employerId);
        if (existingSession != null) {
            log.info("已存在活动会话，sessionId: {}", existingSession.getId());
            return buildSessionVO(existingSession);
        }

        // 5. 创建新会话
        // 5.1 生成 OpenIM 会话ID：按字典序排序拼接 employerId_employeeId
        String openimSessionId = buildOpenimSessionId(employerId, employeeId);

        // 5.2 创建会话实体
        Session newSession = Session.builder()
                .recruitmentId(recruitmentId)
                .employeeId(employeeId)
                .employerId(employerId)
                .openimSessionId(openimSessionId)
                .status(1) // 1-活动
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        sessionMapper.insertSession(newSession);
        log.info("创建新会话成功，sessionId: {}, openimSessionId: {}", newSession.getId(), openimSessionId);

        // 6. 返回结果
        return buildSessionVO(newSession);
    }

    /**
     * @description 获取当前用户的会话列表
     * @author CyberCaelum
     * @date 2026/3/1
     * @return java.util.List<org.cybercaelum.household_management.pojo.vo.SessionCreateVO>
     **/
    @Override
    public List<SessionCreateVO> getUserSessions() {
        Long currentUserId = BaseContext.getUserId();
        List<Session> sessions = sessionMapper.selectUserSessions(currentUserId);
        
        List<SessionCreateVO> result = new ArrayList<>();
        for (Session s : sessions) {
            result.add(buildSessionVO(s));
        }
        return result;
    }

    /**
     * @description 关闭会话
     * @author CyberCaelum
     * @date 2026/3/1
     * @param sessionId 会话id
     **/
    @Override
    public void closeSession(Long sessionId) {
        Long currentUserId = BaseContext.getUserId();
        
        // 查询会话
        Session session = sessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BaseException("会话不存在");
        }
        
        // 验证权限（只有会话双方可以关闭）
        if (!Objects.equals(currentUserId, session.getEmployeeId()) && 
            !Objects.equals(currentUserId, session.getEmployerId())) {
            throw new BaseException("无权操作此会话");
        }
        
        sessionMapper.updateSessionStatus(sessionId, 0);
        log.info("用户 {} 关闭会话 {}", currentUserId, sessionId);
    }

    /**
     * @description 构建 OpenIM 会话ID
     * 规则：将雇主ID和雇员ID按字典序排序后拼接，确保唯一性
     * @author CyberCaelum
     * @date 2026/3/1
     * @param employerId 雇主id
     * @param employeeId 雇员id
     * @return java.lang.String
     **/
    private String buildOpenimSessionId(Long employerId, Long employeeId) {
        String empIdStr = String.valueOf(employerId);
        String emp2IdStr = String.valueOf(employeeId);
        
        // 按字典序排序拼接，确保同一会话的ID一致
        if (empIdStr.compareTo(emp2IdStr) < 0) {
            return empIdStr + "_" + emp2IdStr;
        } else {
            return emp2IdStr + "_" + empIdStr;
        }
    }

    /**
     * @description 构建会话VO（包含用户信息）
     * @author CyberCaelum
     * @date 2026/3/1
     * @param session 会话实体
     * @return org.cybercaelum.household_management.pojo.vo.SessionCreateVO
     **/
    private SessionCreateVO buildSessionVO(Session session) {
        // 查询雇主信息
        User employer = userMapper.getById(session.getEmployerId().intValue());
        // 查询雇员信息
        User employee = userMapper.getById(session.getEmployeeId().intValue());

        return SessionCreateVO.builder()
                .sessionId(session.getId())
                .openimSessionId(session.getOpenimSessionId())
                .status(session.getStatus())
                .employerId(session.getEmployerId())
                .employerName(employer != null ? employer.getUsername() : "")
                .employerAvatar(employer != null ? employer.getProfileUrl() : "")
                .employeeId(session.getEmployeeId())
                .employeeName(employee != null ? employee.getUsername() : "")
                .employeeAvatar(employee != null ? employee.getProfileUrl() : "")
                .build();
    }
}
