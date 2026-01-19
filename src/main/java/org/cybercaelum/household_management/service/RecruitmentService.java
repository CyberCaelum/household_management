package org.cybercaelum.household_management.service;

import org.cybercaelum.household_management.pojo.dto.RecruitmentDTO;
import org.cybercaelum.household_management.pojo.entity.Recruitment;
import org.springframework.stereotype.Service;

public interface RecruitmentService {
    void addRecruitment(RecruitmentDTO recruitmentDTO);

    void updateRecruitmentStatus(int status, Long recruitmentId);
}
