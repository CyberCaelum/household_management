package org.cybercaelum.household_management.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.cybercaelum.household_management.pojo.entity.Session;

import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 会话数据访问层
 * @date 2026/3/1
 */
@Mapper
public interface SessionMapper {

    /**
     * @description 根据招募ID、雇员ID、雇主ID查询活动状态的会话
     * @author CyberCaelum
     * @date 2026/3/1
     * @param recruitmentId 招募id
     * @param employeeId 雇员id
     * @param employerId 雇主id
     * @return org.cybercaelum.household_management.pojo.entity.session
     **/
    @Select("select id, recruitment_id, employee_id, employer_id, openim_session_id, status, create_time, update_time " +
            "from session where recruitment_id = #{recruitmentId} and employee_id = #{employeeId} and employer_id = #{employerId} and status = 1")
    Session selectActiveSession(Long recruitmentId, Long employeeId, Long employerId);

    /**
     * @description 根据id查询会话
     * @author CyberCaelum
     * @date 2026/3/1
     * @param id 会话主键
     * @return org.cybercaelum.household_management.pojo.entity.session
     **/
    @Select("select id, recruitment_id, employee_id, employer_id, openim_session_id, status, create_time, update_time " +
            "from session where id = #{id}")
    Session selectById(Long id);

    /**
     * @description 插入新会话
     * @author CyberCaelum
     * @date 2026/3/1
     * @param session 会话信息
     **/
    @Insert("insert into session(recruitment_id, employee_id, employer_id, openim_session_id, status, create_time, update_time) " +
            "values(#{recruitmentId}, #{employeeId}, #{employerId}, #{openimSessionId}, #{status}, #{createTime}, #{updateTime})")
    void insertSession(Session session);

    /**
     * @description 更新会话状态
     * @author CyberCaelum
     * @date 2026/3/1
     * @param id 会话id
     * @param status 状态
     **/
    @Update("update session set status = #{status} where id = #{id}")
    void updateSessionStatus(Long id, Integer status);

    /**
     * @description 查询用户的所有会话列表（作为雇员或雇主）
     * @author CyberCaelum
     * @date 2026/3/1
     * @param userId 用户id
     * @return java.util.List<org.cybercaelum.household_management.pojo.entity.session>
     **/
    @Select("select id, recruitment_id, employee_id, employer_id, openim_session_id, status, create_time, update_time " +
            "from session where (employee_id = #{userId} or employer_id = #{userId}) and status = 1 " +
            "order by update_time desc")
    List<Session> selectUserSessions(Long userId);
}
