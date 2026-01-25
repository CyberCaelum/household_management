package org.cybercaelum.household_management.mapper;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.cybercaelum.household_management.annotation.AutoFill;
import org.cybercaelum.household_management.enumeration.OperationType;
import org.cybercaelum.household_management.pojo.dto.RecruitmentDTO;
import org.cybercaelum.household_management.pojo.dto.RecruitmentPageDTO;
import org.cybercaelum.household_management.pojo.entity.Recruitment;
import org.cybercaelum.household_management.pojo.vo.RecruitmentVO;

import java.util.List;

@Mapper
public interface RecruitmentMapper {
    /**
     * @description 新增招募
     * @author CyberCaelum
     * @date 下午8:40 2025/11/10
     * @param recruitment 招募
     **/
    @AutoFill(value = OperationType.INSERT)
    void insertRecruitment(Recruitment recruitment);

    /**
     * @description 通过id查找招募
     * @author CyberCaelum
     * @date 下午3:54 2026/1/19
     * @param recruitmentId 招募id
     * @return org.cybercaelum.household_management.pojo.entity.Recruitment
     **/
    @Select("select * from recruitment where id = #{recruitmentId}")
    Recruitment selectRecruitmentById(Long recruitmentId);

    /**
     * @description 修改招募
     * @author CyberCaelum
     * @date 下午4:07 2026/1/19
     * @param recruitment 招募信息
     **/
    @AutoFill(value = OperationType.UPDATE)
    void updateRecruitment(Recruitment recruitment);

    /**
     * @description 分页查询招募
     * @author CyberCaelum
     * @date 下午4:32 2026/1/20
     * @param recruitmentPageDTO
     * @return com.github.pagehelper.Page<org.cybercaelum.household_management.pojo.vo.RecruitmentVO>
     **/
    Page<RecruitmentVO> pageRecruitment(RecruitmentPageDTO recruitmentPageDTO);

    /**
     * @description 删除招募
     * @author CyberCaelum
     * @date 下午3:28 2026/1/23
     * @param ids id列表
     **/
    void deleteRecruitment(List<Long> ids);
}
