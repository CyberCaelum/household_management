package org.cybercaelum.household_management.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.cybercaelum.household_management.pojo.dto.StaffPageDTO;
import org.cybercaelum.household_management.pojo.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * @description 通过电话号查找用户
     * @author CyberCaelum
     * @date 下午8:59 2025/10/20
     * @param phoneNumber 电话号
     * @return org.cybercaelum.household_management.pojo.entity.User
     **/
    @Select("select * from user where phone_number = #{phoneNumber}")
    User getByPhoneNumber(String phoneNumber);

    /**
     * @description 通过用户名查找用户
     * @author CyberCaelum
     * @date 下午9:00 2025/10/20
     * @param username 用户名
     * @return org.cybercaelum.household_management.pojo.entity.User
     **/
    @Select("select * from user where username = #{username}")
    User getByUserName(String username);

    /**
     * @description 插入新用户
     * @author CyberCaelum
     * @date 下午9:22 2025/10/20
     * @param user 电话号，用户名，密码，创建时间，状态，角色，头像地址
     **/
    void insertNewUser(User user);

    /**
     * @description 通过id查找用户
     * @author CyberCaelum
     * @date 下午7:40 2025/10/23
     * @param id 主键
     * @return org.cybercaelum.household_management.pojo.entity.User
     **/
    @Select("select * from user where id = #{id}")
    User getById(Integer id);

    /**
     * @description 更新用户的用户名，密码，手机号，头像
     * @author CyberCaelum
     * @date 下午7:50 2025/10/23
     * @param user 用户类
     **/
    @Update("update user set phone_number = #{phoneNumber},username = #{username},password = #{password},profile_url = #{profileUrl} where id = #{id}")
    void updateUser(User user);

    /**
     * @description 注销账户
     * @author CyberCaelum
     * @date 下午8:33 2025/10/23
     * @param userId 用户主键
     **/
    @Update("update user set status = 0 where id = #{userid}")
    void deleteUserById(Long userId);

    /**
     * @description 员工分页查询（排除普通用户）
     * @author CyberCaelum
     * @date 2026/3/24
     * @param staffPageDTO 分页查询条件
     * @return java.util.List<org.cybercaelum.household_management.pojo.entity.User>
     **/
    List<User> pageStaff(StaffPageDTO staffPageDTO);

    /**
     * @description 重置员工密码
     * @author CyberCaelum
     * @date 2026/3/24
     * @param staffId 员工id
     * @param password 加密后的密码
     **/
    void resetPassword(@Param("staffId") Long staffId, @Param("password") String password);

    /**
     * @description 修改员工账号状态
     * @author CyberCaelum
     * @date 2026/3/24
     * @param staffId 员工id
     * @param status 账号状态
     **/
    void updateStatus(@Param("staffId") Long staffId, @Param("status") Integer status);
}
