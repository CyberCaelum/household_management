package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.constant.MessageConstant;
import org.cybercaelum.household_management.context.BaseContext;
import org.cybercaelum.household_management.exception.*;
import org.cybercaelum.household_management.mapper.RecruitmentMapper;
import org.cybercaelum.household_management.pojo.dto.RecruitmentDTO;
import org.cybercaelum.household_management.pojo.entity.Recruitment;
import org.cybercaelum.household_management.service.RecruitmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

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

    /**
     * @description 修改招募状态
     * @author CyberCaelum
     * @date 下午3:52 2026/1/19
     * @param status 招募状态，0删除，1发布，2隐藏，3结束
     * @param recruitmentId 招募主键
     **/
    @Override
    public void updateRecruitmentStatus(int status, Long recruitmentId) {
        //查找招募
        Recruitment recruitment = recruitmentMapper.selectRecruitmentById(recruitmentId);
        if (recruitment == null) {
            throw new RequirementNullException("招募不存在");
        }
        //判断是否是本人
        if (!Objects.equals(BaseContext.getUserId(), recruitment.getUserId())) {
            throw new PermissionException("招募状态修改失败");
        }
        //判断状态是否正确
        if (status != 0 && status != 1 && status != 2 && status != 3){
            throw new RequirementStatusException("招募状态错误");
        }
        //修改招募状态
        recruitment.setStatus(status);
        recruitmentMapper.updateRecruitment(recruitment);
    }
}
