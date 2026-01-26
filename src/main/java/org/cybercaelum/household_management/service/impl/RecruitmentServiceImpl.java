package org.cybercaelum.household_management.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.context.BaseContext;
import org.cybercaelum.household_management.exception.*;
import org.cybercaelum.household_management.mapper.RecruitmentMapper;
import org.cybercaelum.household_management.pojo.dto.RecruitmentDTO;
import org.cybercaelum.household_management.pojo.dto.RecruitmentPageDTO;
import org.cybercaelum.household_management.pojo.entity.PageResult;
import org.cybercaelum.household_management.pojo.entity.Recruitment;
import org.cybercaelum.household_management.pojo.vo.RecruitmentVO;
import org.cybercaelum.household_management.service.RecruitmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
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
        //判断招募是否存在
        if (recruitment == null) {
            throw new RequirementNullException("招募不存在");
        }
        //判断是否是本人
        if (!Objects.equals(BaseContext.getUserId(), recruitment.getUserId())) {
            throw new PermissionException("没有权限");
        }
        //判断状态是否正确
        if (status != 0 && status != 1 && status != 2 && status != 3){
            throw new RequirementStatusException("招募状态错误");
        }
        //修改招募状态
        recruitment.setStatus(status);
        recruitmentMapper.updateRecruitment(recruitment);
    }

    /**
     * @description 修改招募信息
     * @author CyberCaelum
     * @date 下午3:35 2026/1/20
     * @param recruitmentDTO 招募信息
     **/
    @Override
    public void updateRecruitment(RecruitmentDTO recruitmentDTO) {
        //查找招募
        Recruitment recruitment = recruitmentMapper.selectRecruitmentById(recruitmentDTO.getId());
        //判断招募是否存在
        if (recruitment == null) {
            throw new RequirementNullException("招募不存在");
        }
        //判断是否是本人
        if (!Objects.equals(BaseContext.getUserId(), recruitment.getUserId())) {
            throw new PermissionException("没有权限");
        }
        //修改招募
        recruitmentMapper.updateRecruitment(recruitment);
    }

    /**
     * @param recruitmentPageDTO 分页信息
     * @return java.util.List<org.cybercaelum.household_management.pojo.vo.RecruitmentVO>
     * @description 分页查询招募信息
     * @author CyberCaelum
     * @date 下午4:20 2026/1/20
     **/
    @Override
    public PageResult pageRecruitment(RecruitmentPageDTO recruitmentPageDTO) {
        PageHelper.startPage(recruitmentPageDTO.getPage(),recruitmentPageDTO.getPageSize());
        Page<RecruitmentVO> page =recruitmentMapper.pageRecruitment(recruitmentPageDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * @description 删除招募
     * @author CyberCaelum
     * @date 下午3:27 2026/1/23
     * @param ids 招募id列表
     **/
    @Override
    public void deleteRecruitment(List<Long> ids) {
        for (Long id : ids) {
            //查找招募
            Recruitment recruitment = recruitmentMapper.selectRecruitmentById(id);
            //判断招募是否存在
            if (recruitment == null) {
                throw new RequirementNullException("招募不存在");
            }
            //判断是否是本人
            if (!Objects.equals(BaseContext.getUserId(), recruitment.getUserId())) {
                throw new PermissionException("没有权限");
            }
        }
        recruitmentMapper.deleteRecruitment(ids);
    }

    /**
     * @description 根据招募id查询招募信息
     * @author CyberCaelum
     * @date 下午6:55 2026/1/26
     * @param id 招募id
     * @return org.cybercaelum.household_management.pojo.vo.RecruitmentVO
     **/
    @Override
    public RecruitmentVO getRecruitment(Long id) {
        return recruitmentMapper.selectRecruitmentUserInfoById(id);
    }
}
