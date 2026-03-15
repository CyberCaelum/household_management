package org.cybercaelum.household_management.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.cybercaelum.household_management.pojo.entity.Settlement;

import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 结算记录Mapper
 * @date 2026/3/15
 */
@Mapper
public interface SettlementMapper {

    /**
     * 插入结算记录
     */
    void insert(Settlement settlement);

    /**
     * 根据ID查询结算记录
     */
    @Select("SELECT * FROM settlement WHERE id = #{id}")
    Settlement selectById(Long id);

    /**
     * 根据订单ID查询结算记录
     */
    @Select("SELECT * FROM settlement WHERE order_id = #{orderId}")
    Settlement selectByOrderId(Long orderId);

    /**
     * 更新结算记录
     */
    void update(Settlement settlement);

    /**
     * 查询待结算的记录
     */
    @Select("SELECT * FROM settlement WHERE status = 0")
    List<Settlement> selectPendingSettlements();
}
