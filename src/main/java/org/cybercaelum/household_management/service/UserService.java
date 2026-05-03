package org.cybercaelum.household_management.service;

import org.cybercaelum.household_management.pojo.dto.CreateStaffDTO;
import org.cybercaelum.household_management.pojo.dto.StaffPageDTO;
import org.cybercaelum.household_management.pojo.dto.UserLoginDTO;
import org.cybercaelum.household_management.pojo.dto.UserRegisterDTO;
import org.cybercaelum.household_management.pojo.dto.UserUpdateDTO;
import org.cybercaelum.household_management.pojo.entity.PageResult;
import org.cybercaelum.household_management.pojo.vo.UserInfoVO;
import org.cybercaelum.household_management.pojo.vo.UserLoginVO;
import org.springframework.stereotype.Service;

public interface UserService {
    /**
     * @description 用户登录
     * @author CyberCaelum
     * @date 下午8:46 2025/10/20
     * @param userLoginDTO 电话号，密码
     * @return org.cybercaelum.household_management.pojo.vo.UserLoginVO
     **/
    UserLoginVO login(UserLoginDTO userLoginDTO);
    /**
     * @description 用户注册
     * @author CyberCaelum
     * @date 下午8:47 2025/10/20
     * @param userRegisterDTO 用户名，电话号，密码
     * @return org.cybercaelum.household_management.pojo.vo.UserLoginVO
     **/
    UserLoginVO register(UserRegisterDTO userRegisterDTO);
    /**
     * @description 更新用户信息
     * @author CyberCaelum
     * @date 下午7:36 2025/10/23
     * @param userUpdateDTO id，用户名，电话号，密码
     * @return org.cybercaelum.household_management.pojo.vo.UserLoginVO
     **/
    UserLoginVO updateUser(UserUpdateDTO userUpdateDTO);
    /**
     * @description 用户注销
     * @author CyberCaelum
     * @date 下午8:08 2025/10/23
     **/
    void cancel();

    void createStaff(CreateStaffDTO createStaffDTO);

    /**
     * @description 员工分页查询
     * @author CyberCaelum
     * @date 2026/3/24
     * @param staffPageDTO 分页查询条件
     * @return org.cybercaelum.household_management.pojo.entity.PageResult
     **/
    PageResult pageStaff(StaffPageDTO staffPageDTO);

    /**
     * @description 重置员工密码
     * @author CyberCaelum
     * @date 2026/3/24
     * @param staffId 员工id
     * @param newPassword 新密码
     **/
    void resetPassword(Long staffId, String newPassword);

    /**
     * @description 修改员工账号状态
     * @author CyberCaelum
     * @date 2026/3/24
     * @param staffId 员工id
     * @param status 账号状态
     **/
    void updateStatus(Long staffId, Integer status);

    /**
     * @description 查询当前登录用户信息
     * @author CyberCaelum
     * @date 2025/10/23
     * @return org.cybercaelum.household_management.pojo.vo.UserInfoVO
     **/
    UserInfoVO getUserInfo();
}
