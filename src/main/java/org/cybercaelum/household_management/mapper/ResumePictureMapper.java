package org.cybercaelum.household_management.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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

}
