package org.cybercaelum.household_management.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.cybercaelum.household_management.pojo.dto.RecruitmentDTO;

@Mapper
public interface RecruitmentMapper {
    void insertRecruitment(RecruitmentDTO recruitmentDTO);
}
