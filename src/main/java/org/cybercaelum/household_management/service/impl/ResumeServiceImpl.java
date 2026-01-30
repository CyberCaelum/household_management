package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.context.BaseContext;
import org.cybercaelum.household_management.exception.IsResumeExistException;
import org.cybercaelum.household_management.mapper.ResumeMapper;
import org.cybercaelum.household_management.mapper.ResumePictureMapper;
import org.cybercaelum.household_management.pojo.dto.ResumeDTO;
import org.cybercaelum.household_management.pojo.entity.Resume;
import org.cybercaelum.household_management.pojo.entity.ResumePicture;
import org.cybercaelum.household_management.pojo.vo.ResumeVO;
import org.cybercaelum.household_management.service.ResumeService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
        log.info("简介{}",resume);
        //存入数据库
        Long resumeId = resumeMapper.addResume(resume);
        //编辑图片数据
        List<ResumePicture> resumePictureList = new ArrayList<>();
        for (String picture : resumeDTO.getPictures()){
            ResumePicture resumePicture = ResumePicture.builder()
                    .url(picture)
                    .resumeId(resumeId)
                    .status(1)
                    .build();
            resumePictureList.add(resumePicture);
        }
        //存入图片数据
        resumePictureMapper.addResumePicture(resumePictureList);
    }

    @Override
    public ResumeVO getResume(Long id) {
        //判断是否是用户自己查看
        Long userId = BaseContext.getUserId();
        ResumeVO resumeVO = resumeMapper.getResumeByUserId(id);
        resumeVO.setPicture(resumePictureMapper.getPicturesByUserId(id));
        //不是本人
        if (userId == null || !userId.equals(resumeVO.getUserId())){
            //判断可见性
            if (resumeVO.getVisibility() ==0){
                return null;
            }
        }
        return resumeVO;
    }
}
