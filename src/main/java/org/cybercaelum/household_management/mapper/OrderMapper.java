package org.cybercaelum.household_management.mapper;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.cybercaelum.household_management.pojo.entity.Order;

@Mapper
public interface OrderMapper {

    /**
     * @description 插入订单信息
     * @author CyberCaelum
     * @date 上午10:42 2026/3/12
     * @param order 订单信息
     **/
    void insertOrder(Order order);

    /**
     * @description 查找历史订单（支持雇主和被雇者双角色查询）
     * @author CyberCaelum
     * @date 上午10:42 2026/3/12
     * @param userId 用户id
     * @param status 订单状态
     * @return com.github.pagehelper.Page<org.cybercaelum.household_management.pojo.entity.Order>
     **/
    Page<Order> history(@Param("userId") Long userId, @Param("status") Integer status);
}
