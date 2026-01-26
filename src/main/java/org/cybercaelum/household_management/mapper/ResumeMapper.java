package org.cybercaelum.household_management.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.cybercaelum.household_management.annotation.AutoFill;
import org.cybercaelum.household_management.enumeration.OperationType;
import org.cybercaelum.household_management.pojo.dto.ResumeDTO;
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
     * @description 新增简历
     * @author CyberCaelum
     * @date 下午7:37 2026/1/25
     * @param resumeDTO 简历信息
     **/
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into resume (user_id, resume_data, create_time, update_time, visibility) " +
            "VALUE (#{userId},#{resumeData},#{createDate},#{updateDate},#{visibility})")
    void addResume(Resume resumeDTO);

    /**
     * @description 根据用户id查询简历信息
     * @author CyberCaelum
     * @date 下午7:12 2026/1/26
     * @param id 用户id
     * @return org.cybercaelum.household_management.pojo.vo.ResumeVO
     **/
    @Select("select resume_data,create_time,update_time from resume where user_id = #{id}")
    ResumeVO getResumeByUserId(Long id);
}
