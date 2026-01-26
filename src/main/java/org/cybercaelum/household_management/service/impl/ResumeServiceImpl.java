package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.context.BaseContext;
import org.cybercaelum.household_management.mapper.ResumeMapper;
import org.cybercaelum.household_management.pojo.dto.ResumeDTO;
import org.cybercaelum.household_management.pojo.entity.Resume;
import org.cybercaelum.household_management.pojo.vo.ResumeVO;
import org.cybercaelum.household_management.service.ResumeService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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

    @Override
    public void addResume(ResumeDTO resumeDTO) {
        Resume resume = new Resume();
        //复制属性
        BeanUtils.copyProperties(resumeDTO, resume);
        //设置用户id
        resume.setUserId(BaseContext.getUserId());
        //存入数据库
        resumeMapper.addResume(resume);
    }

    @Override
    public ResumeVO getResume(Long id) {
        ResumeVO resumeVO = resumeMapper.getResumeByUserId(id);
        return resumeVO;
    }
}
