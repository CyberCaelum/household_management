package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.constant.MessageConstant;
import org.cybercaelum.household_management.exception.MineSalaryErrorException;
import org.cybercaelum.household_management.exception.TitleIsEmptyException;
import org.cybercaelum.household_management.exception.TitleTooLongException;
import org.cybercaelum.household_management.mapper.RecruitmentMapper;
import org.cybercaelum.household_management.pojo.dto.RecruitmentDTO;
import org.cybercaelum.household_management.pojo.entity.Recruitment;
import org.cybercaelum.household_management.service.RecruitmentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 招募信息相关服务
 * @date 2025/11/9 下午1:28
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RecruitmentServiceImpl implements RecruitmentService {

    private final RecruitmentMapper recruitmentMapper;

    @Override
    public void addRecruitment(RecruitmentDTO recruitmentDTO) {
        recruitmentMapper.insertRecruitment(recruitmentDTO);
    }
}
