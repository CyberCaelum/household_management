package org.cybercaelum.household_management.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.cybercaelum.household_management.annotation.AutoFill;
import org.cybercaelum.household_management.enumeration.OperationType;
import org.cybercaelum.household_management.pojo.dto.RecruitmentDTO;
import org.cybercaelum.household_management.pojo.entity.Recruitment;

@Mapper
public interface RecruitmentMapper {
    /**
     * @description 新增招募
     * @author CyberCaelum
     * @date 下午8:40 2025/11/10
     * @param recruitment 招募
     **/
    @AutoFill(value = OperationType.INSERT)
    void insertRecruitment(Recruitment recruitment);
}
