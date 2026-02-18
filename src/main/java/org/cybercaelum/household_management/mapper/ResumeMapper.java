package org.cybercaelum.household_management.mapper;

import org.apache.ibatis.annotations.*;
import org.cybercaelum.household_management.annotation.AutoFill;
import org.cybercaelum.household_management.enumeration.OperationType;
import org.cybercaelum.household_management.pojo.entity.Resume;
import org.cybercaelum.household_management.pojo.vo.ResumeVO;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 简介Mapper
 * @date 2026/1/23 下午4:25
 */
@Mapper
public interface ResumeMapper {

    /**
     * @param resumeDTO 简历信息
     * @return
     * @description 新增简历
     * @author CyberCaelum
     * @date 下午7:37 2026/1/25
     */
    @AutoFill(value = OperationType.INSERT)
    Long addResume(Resume resumeDTO);

    /**
     * @description 根据用户id查询简历信息
     * @author CyberCaelum
     * @date 下午7:12 2026/1/26
     * @param id 用户id
     * @return org.cybercaelum.household_management.pojo.vo.ResumeVO
     **/
    @Select("select id,resume_data,create_time,update_time,visibility from resume where user_id = #{id}")
    ResumeVO getResumeByUserId(Long id);

    /**
     * @description 根据用户id查询简历
     * @author CyberCaelum
     * @date 2026/2/18
     * @param userId 用户id
     * @return org.cybercaelum.household_management.pojo.entity.Resume
     **/
    @Select("select * from resume where user_id = #{userId}")
    Resume getByUserId(Long userId);

    /**
     * @description 更新简历信息
     * @author CyberCaelum
     * @date 2026/2/18
     * @param resume 简历信息
     **/
    @AutoFill(value = OperationType.UPDATE)
    void updateResume(Resume resume);

    /**
     * @description 根据id查询简历
     * @author CyberCaelum
     * @date 2026/2/18
     * @param id 简历id
     * @return org.cybercaelum.household_management.pojo.entity.Resume
     **/
    @Select("select * from resume where id = #{id}")
    Resume getById(Long id);

}
