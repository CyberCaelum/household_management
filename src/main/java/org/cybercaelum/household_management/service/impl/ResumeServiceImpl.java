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
        //检查图片数量限制（最多5张）
        List<String> pictures = resumeDTO.getPictures();
        if (pictures != null && pictures.size() > 5) {
            throw new PictureCountLimitException("简历图片最多只能上传5张");
        }

        Resume resume = new Resume();
        //复制属性
        BeanUtils.copyProperties(resumeDTO, resume);
        //设置用户id
        resume.setUserId(userId);
        log.info("简介{}", resume);
        //存入数据库
        Long resumeId = resumeMapper.addResume(resume);
        //编辑图片数据
        List<ResumePicture> resumePictureList = new ArrayList<>();
        if (pictures != null) {
            for (String picture : pictures) {
                ResumePicture resumePicture = ResumePicture.builder()
                        .url(picture)
                        .resumeId(resumeId)
                        .userId(userId)
                        .status(1)
                        .build();
                resumePictureList.add(resumePicture);
            }
            //存入图片数据
            if (!resumePictureList.isEmpty()) {
                resumePictureMapper.addResumePicture(resumePictureList);
            }
        }
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
        if (userId == null || !userId.equals(resumeVO.getUserId())) {
            //判断可见性
            if (resumeVO.getVisibility() == 0) {
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

        //处理图片更新：先删除旧图片，再添加新图片
        if (resumeDTO.getPictures() != null) {
            //检查图片数量限制（最多5张）
            if (resumeDTO.getPictures().size() > 5) {
                throw new PictureCountLimitException("简历图片最多只能上传5张");
            }
            //删除旧图片
            resumePictureMapper.deleteByResumeId(resume.getId());
            //添加新图片
            List<ResumePicture> resumePictureList = new ArrayList<>();
            for (String picture : resumeDTO.getPictures()) {
                ResumePicture resumePicture = ResumePicture.builder()
                        .url(picture)
                        .resumeId(resume.getId())
                        .status(1)
                        .userId(userId)
                        .build();
                resumePictureList.add(resumePicture);
            }
            resumePictureMapper.addResumePicture(resumePictureList);
        }
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
