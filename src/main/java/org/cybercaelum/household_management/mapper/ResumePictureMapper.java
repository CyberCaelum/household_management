package org.cybercaelum.household_management.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.cybercaelum.household_management.annotation.AutoFill;
import org.cybercaelum.household_management.enumeration.OperationType;
import org.cybercaelum.household_management.pojo.entity.ResumePicture;

import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 简介图片Mapper
 * @date 2026/1/30 下午4:31
 */
@Mapper
public interface ResumePictureMapper {

    /**
     * @description 插入图片
     * @author CyberCaelum
     * @date 下午8:23 2026/1/30
     * @param resumePictureList 简历图片列表
     **/
    @AutoFill(value = OperationType.INSERT)
    void addResumePicture(@Param("pictures") List<ResumePicture> resumePictureList);

    /**
     * @description 根据简历id逻辑删除图片（将status更新为0）
     * @author CyberCaelum
     * @date 2026/2/18
     * @param resumeId 简历id
     **/
    void deleteByResumeId(Long resumeId);

}
