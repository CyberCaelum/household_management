package org.cybercaelum.household_management.service;

import org.cybercaelum.household_management.pojo.dto.UserLoginDTO;
import org.cybercaelum.household_management.pojo.dto.UserRegisterDTO;
import org.cybercaelum.household_management.pojo.vo.UserLoginVO;
import org.springframework.stereotype.Service;

@Service
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
}
