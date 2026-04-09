package org.cybercaelum.household_management.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.cybercaelum.household_management.pojo.entity.CancelApplication;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 取消申请Mapper
 * @date 2026/3/15
 */
@Mapper
public interface CancelApplicationMapper {

    /**
     * 插入取消申请
     */
    void insert(CancelApplication cancelApplication);

    /**
     * 根据ID查询取消申请
     */
    @Select("SELECT * FROM cancel_application WHERE id = #{id}")
    CancelApplication selectById(Long id);

    /**
     * 根据订单ID查询有效的取消申请
     */
    @Select("SELECT * FROM cancel_application WHERE order_id = #{orderId} AND status IN (1, 4) ORDER BY create_time DESC LIMIT 1")
    CancelApplication selectActiveByOrderId(Long orderId);

    /**
     * 更新取消申请
     */
    void update(CancelApplication cancelApplication);

    /**
     * 查询超时的取消申请（待对方确认且已超过超时时间）
     */
    List<CancelApplication> selectTimeoutApplications(@Param("now") LocalDateTime now);

    /**
     * 查询平台介入中的申请
     */
    @Select("SELECT * FROM cancel_application WHERE status = 4")
    List<CancelApplication> selectPlatformProcessing();

    /**
     * @description 查询所有待平台处理的取消申请（status = 4 平台介入处理中）
     * @author CyberCaelum
     * @date 2026/4/9
     * @return java.util.List<org.cybercaelum.household_management.pojo.entity.CancelApplication>
     **/
    @Select("SELECT * FROM cancel_application WHERE status = 4 ORDER BY create_time DESC")
    List<CancelApplication> selectPendingCancelApplications();

    /**
     * @description 统计待平台处理的取消申请数量（status = 4 平台介入处理中）
     * @author CyberCaelum
     * @date 2026/4/9
     * @return java.lang.Integer
     **/
    @Select("SELECT COUNT(*) FROM cancel_application WHERE status = 4")
    Integer countPendingCancelApplications();
}
