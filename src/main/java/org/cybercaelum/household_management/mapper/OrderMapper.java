package org.cybercaelum.household_management.mapper;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.cybercaelum.household_management.pojo.entity.Order;

@Mapper
public interface OrderMapper {

    /**
     * @description 根据订单号查询订单
     * @author CyberCaelum
     * @date 上午10:35 2026/3/13
     * @param orderNumber 订单号
     * @return org.cybercaelum.household_management.pojo.entity.Order
     **/
    @Select("select * from order where order_number = #{orderNumber}")
    Order getOrderByNumber(String orderNumber);

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

    /**
     * @description 通过订单id获取订单状态
     * @author CyberCaelum
     * @date 2026/3/12
     * @param orderId 订单id
     * @return java.lang.Integer
     **/
    @Select("select status from `order` where id = #{id}")
    Integer getOrderStatusById(Long orderId);

    /**
     * @description 修改订单
     * @author CyberCaelum
     * @date 上午10:49 2026/3/13
     * @param order 订单信息
     **/
    void updateOrder(Order order);

    /**
     * @description 通过主键查找订单
     * @author CyberCaelum
     * @date 上午11:02 2026/3/13
     * @param id 主键
     * @return org.cybercaelum.household_management.pojo.entity.Order
     **/
    @Select("select * from order where id = #{id}")
    Order getOrderById(Long id);
}
