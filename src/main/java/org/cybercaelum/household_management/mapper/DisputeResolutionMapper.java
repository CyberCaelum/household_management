package org.cybercaelum.household_management.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.cybercaelum.household_management.pojo.entity.DisputeResolution;

import java.util.List;

@Mapper
public interface DisputeResolutionMapper {

    /**
     * @description 插入争议处理
     * @author CyberCaelum
     * @date 上午8:36 2026/3/19
     * @param disputeResolution 争议处理信息
     **/
    void insertDisputeResolution(DisputeResolution disputeResolution);

    /**
     * @description 更新争议处理信息
     * @author CyberCaelum
     * @date 上午8:37 2026/3/19
     * @param disputeResolution 争议处理信息
     **/
    void updateDisputeResolution(DisputeResolution disputeResolution);

    /**
     * @description 通过订单id查找处理结果
     * @author CyberCaelum
     * @date 上午8:54 2026/3/19
     * @param id 订单id
     * @param sourceType 争议来源
     * @return org.cybercaelum.household_management.pojo.entity.DisputeResolution
     **/
    @Select("select * from dispute_resolution where order_id = #{id} and source_type = #{sourceType}")
    DisputeResolution selectDisputeResolutionByOrderId(Long id,Integer sourceType);

    @Select("select * from dispute_resolution where id = #{disputeId}")
    DisputeResolution selectById(Long disputeId);

    /**
     * @description 通过来源ID和来源类型查询争议处理
     * @author CyberCaelum
     * @date 2026/4/18
     * @param sourceId 来源记录id
     * @param sourceType 来源类型
     * @return org.cybercaelum.household_management.pojo.entity.DisputeResolution
     **/
    @Select("select * from dispute_resolution where source_id = #{sourceId} and source_type = #{sourceType} order by created_time desc limit 1")
    DisputeResolution selectBySourceId(Long sourceId, Integer sourceType);

    /**
     * @description 查询所有未裁决的争议记录
     * @author CyberCaelum
     * @date 2026/4/18
     * @return java.util.List<org.cybercaelum.household_management.pojo.entity.DisputeResolution>
     **/
    @Select("select * from dispute_resolution where decision is null order by created_time desc")
    List<DisputeResolution> selectPendingDisputes();
}
