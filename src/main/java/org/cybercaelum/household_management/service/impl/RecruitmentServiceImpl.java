package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.constant.MessageConstant;
import org.cybercaelum.household_management.context.BaseContext;
import org.cybercaelum.household_management.exception.MineSalaryErrorException;
import org.cybercaelum.household_management.exception.TitleIsEmptyException;
import org.cybercaelum.household_management.exception.TitleTooLongException;
import org.cybercaelum.household_management.mapper.RecruitmentMapper;
import org.cybercaelum.household_management.pojo.dto.RecruitmentDTO;
import org.cybercaelum.household_management.pojo.entity.Recruitment;
import org.cybercaelum.household_management.service.RecruitmentService;
import org.springframework.beans.BeanUtils;
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

    /**
     * @description 新增招募
     * @author CyberCaelum
     * @date 下午8:49 2025/11/10
     * @param recruitmentDTO 招募信息
     **/
    @Override
    public void addRecruitment(RecruitmentDTO recruitmentDTO) {
        Recruitment recruitment = new Recruitment();
        BeanUtils.copyProperties(recruitmentDTO, recruitment);
        recruitment.setUserId(BaseContext.getUserId());
        recruitmentMapper.insertRecruitment(recruitment);
    }
}
