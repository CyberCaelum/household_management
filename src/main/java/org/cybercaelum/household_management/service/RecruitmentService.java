package org.cybercaelum.household_management.service;

import org.cybercaelum.household_management.pojo.dto.RecruitmentDTO;
import org.cybercaelum.household_management.pojo.dto.RecruitmentPageDTO;
import org.cybercaelum.household_management.pojo.entity.PageResult;
import org.cybercaelum.household_management.pojo.vo.RecruitmentVO;

import java.util.List;

public interface RecruitmentService {
    void addRecruitment(RecruitmentDTO recruitmentDTO);

    void updateRecruitmentStatus(int status, Long recruitmentId);

    void updateRecruitment(RecruitmentDTO recruitmentDTO);

    PageResult pageRecruitment(RecruitmentPageDTO recruitmentPageDTO);

    void deleteRecruitment(List<Long> ids);

    RecruitmentVO getRecruitment(Long id);
}
