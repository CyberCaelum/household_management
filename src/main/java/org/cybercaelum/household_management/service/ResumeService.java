package org.cybercaelum.household_management.service;

import org.cybercaelum.household_management.pojo.dto.ResumeDTO;
import org.cybercaelum.household_management.pojo.vo.ResumeVO;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 简介服务类
 * @date 2026/1/23 下午4:21
 */
public interface ResumeService {
    void addResume(ResumeDTO resumeDTO);

    ResumeVO getResume(Long id);

    /**
     * @description 修改简历信息
     * @author CyberCaelum
     * @date 2026/2/18
     * @param resumeDTO 简历信息
     **/
    void updateResume(ResumeDTO resumeDTO);

    /**
     * @description 修改简历可见性状态
     * @author CyberCaelum
     * @date 2026/2/18
     * @param visibility 可见性，0为不可见，1为可见
     **/
    void updateVisibility(Integer visibility);
}
