package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.context.BaseContext;
import org.cybercaelum.household_management.exception.IsResumeExistException;
import org.cybercaelum.household_management.exception.PictureCountLimitException;
import org.cybercaelum.household_management.exception.ResumeNotFoundException;
import org.cybercaelum.household_management.mapper.ResumeMapper;
import org.cybercaelum.household_management.mapper.ResumePictureMapper;
import org.cybercaelum.household_management.pojo.dto.ResumeDTO;
import org.cybercaelum.household_management.pojo.entity.Resume;
import org.cybercaelum.household_management.pojo.entity.ResumePicture;
import org.cybercaelum.household_management.pojo.vo.ResumeVO;
import org.cybercaelum.household_management.service.ResumeService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 简介服务类
 * @date 2026/1/23 下午4:22
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeMapper resumeMapper;
    private final ResumePictureMapper resumePictureMapper;

    /**
     * @param resumeDTO 简介信息
     * @description 新增简介
     * @author CyberCaelum
     * @date 下午8:26 2026/1/30
     **/
    @Transactional
    @Override
    public void addResume(ResumeDTO resumeDTO) {

        Long userId = BaseContext.getUserId();
        //判断用户是否有简历
        ResumeVO resumeVO = resumeMapper.getResumeByUserId(userId);
        if (resumeVO != null) {//用户有简历，不可新增
            throw new IsResumeExistException("个人简历已存在");
        }

        Resume resume = new Resume();
        //复制属性
        BeanUtils.copyProperties(resumeDTO, resume);
        //设置用户id
        resume.setUserId(userId);
        log.info("简介{}", resume);
        //存入数据库
        resumeMapper.addResume(resume);
    }

    /**
     * @description 通过用户id查找简介
     * @author CyberCaelum
     * @date 2026/2/21
     * @param id 用户主键
     * @return org.cybercaelum.household_management.pojo.vo.ResumeVO
     **/
    @Override
    public ResumeVO getResume(Long id) {
        //判断是否是用户自己查看
        Long userId = BaseContext.getUserId();
        ResumeVO resumeVO = resumeMapper.getResumeByUserId(id);
        if (resumeVO == null) {
            return null;
        }
        //不是本人
        if (userId == null && !userId.equals(resumeVO.getUserId())) {
            //判断可见性
            if (resumeVO.getVisibility() == 0) {
                log.info("简历不可见");
                return null;
            }
        }
        return resumeVO;
    }

    /**
     * @description 修改简历信息
     * @author CyberCaelum
     * @date 2026/2/18
     * @param resumeDTO 简历信息
     **/
    @Transactional
    @Override
    public void updateResume(ResumeDTO resumeDTO) {
        Long userId = BaseContext.getUserId();
        //查询用户简历
        Resume resume = resumeMapper.getByUserId(userId);
        if (resume == null) {
            throw new ResumeNotFoundException("简历不存在");
        }
        //更新简历信息
        resume.setResumeData(resumeDTO.getResumeData());
        resumeMapper.updateResume(resume);
    }

    /**
     * @param visibility 可见性，0为不可见，1为可见
     * @description 修改简历可见性状态
     * @author CyberCaelum
     * @date 2026/2/18
     **/
    @Transactional
    @Override
    public void updateVisibility(Integer visibility) {
        Long userId = BaseContext.getUserId();
        //查询用户简历
        Resume resume = resumeMapper.getByUserId(userId);
        if (resume == null) {
            throw new ResumeNotFoundException("简历不存在");
        }
        //更新可见性状态
        resume.setVisibility(visibility);
        resumeMapper.updateResume(resume);
    }
}
