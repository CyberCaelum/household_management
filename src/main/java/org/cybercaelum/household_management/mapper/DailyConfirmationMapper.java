package org.cybercaelum.household_management.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.cybercaelum.household_management.pojo.entity.DailyConfirmation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 每日服务确认Mapper
 * @date 2026/3/15
 */
@Mapper
public interface DailyConfirmationMapper {

    /**
     * 插入每日确认记录
     */
    void insert(DailyConfirmation dailyConfirmation);

    /**
     * 根据订单ID和服务日期查询确认记录
     */
    @Select("SELECT * FROM daily_confirmation WHERE order_id = #{orderId} AND service_date = #{serviceDate}")
    DailyConfirmation selectByOrderIdAndDate(@Param("orderId") Long orderId, @Param("serviceDate") LocalDate serviceDate);

    /**
     * 根据ID查询确认记录
     */
    @Select("SELECT * FROM daily_confirmation WHERE id = #{id}")
    DailyConfirmation selectById(Long id);

    /**
     * 更新确认记录
     */
    void update(DailyConfirmation dailyConfirmation);

    /**
     * 根据订单ID查询所有确认记录
     */
    @Select("SELECT * FROM daily_confirmation WHERE order_id = #{orderId} ORDER BY service_date")
    List<DailyConfirmation> selectByOrderId(Long orderId);

    /**
     * 查询需要自动确认的记录（服务日期已过且超过24小时未确认）
     */
    List<DailyConfirmation> selectNeedAutoConfirm(@Param("thresholdTime") LocalDateTime thresholdTime);

    /**
     * 统计订单已确认的天数
     */
    @Select("SELECT COUNT(*) FROM daily_confirmation WHERE order_id = #{orderId} AND status IN (1, 3)")
    Integer countConfirmedDays(Long orderId);

    /**
     * @description 批量插入每日确认记录
     * @author CyberCaelum
     * @date 上午10:05 2026/3/16
     * @param list 确认记录列表
     **/
    void batchInsert(List<DailyConfirmation> list);

    /**
     * @description 查询所有待处理的争议记录（status = 2 雇主拒绝/争议）
     * @author CyberCaelum
     * @date 2026/4/9
     * @return java.util.List<org.cybercaelum.household_management.pojo.entity.DailyConfirmation>
     **/
    @Select("SELECT * FROM daily_confirmation WHERE status = 2 ORDER BY create_time DESC")
    List<DailyConfirmation> selectPendingDisputes();

    /**
     * @description 统计待处理的争议数量（status = 2 雇主拒绝/争议）
     * @author CyberCaelum
     * @date 2026/4/9
     * @return java.lang.Integer
     **/
    @Select("SELECT COUNT(*) FROM daily_confirmation WHERE status = 2")
    Integer countPendingDisputes();
}
