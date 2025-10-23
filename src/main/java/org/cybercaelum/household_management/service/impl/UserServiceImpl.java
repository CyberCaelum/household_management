package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.cybercaelum.household_management.constant.JwtClaimsConstant;
import org.cybercaelum.household_management.constant.MessageConstant;
import org.cybercaelum.household_management.constant.RoleConstant;
import org.cybercaelum.household_management.constant.StatusConstant;
import org.cybercaelum.household_management.context.BaseContext;
import org.cybercaelum.household_management.exception.AccountNotFoundException;
import org.cybercaelum.household_management.exception.PasswordErrorException;
import org.cybercaelum.household_management.exception.PhoneNumberUsedException;
import org.cybercaelum.household_management.exception.UsernameExistException;
import org.cybercaelum.household_management.mapper.UserMapper;
import org.cybercaelum.household_management.pojo.dto.UserLoginDTO;
import org.cybercaelum.household_management.pojo.dto.UserRegisterDTO;
import org.cybercaelum.household_management.pojo.dto.UserUpdateDTO;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.cybercaelum.household_management.pojo.entity.User;
import org.cybercaelum.household_management.pojo.vo.UserLoginVO;
import org.cybercaelum.household_management.properties.JwtProperties;
import org.cybercaelum.household_management.service.UserService;
import org.cybercaelum.household_management.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 用户相关业务层
 * @date 2025/10/20 下午7:43
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtProperties jwtProperties;

    /**
     * @description 创建token
     * @author CyberCaelum
     * @date 下午7:45 2025/10/23
     * @param user 用户类
     * @return org.cybercaelum.household_management.pojo.vo.UserLoginVO
     **/
    private UserLoginVO newLogin(User user){
        //创建token
        Map<String,Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        claims.put(JwtClaimsConstant.USER_ROLE,user.getRole());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);
        return UserLoginVO.builder()
                .username(user.getUsername())
                .profileUrl(user.getProfileUrl())
                .token(token)
                .build();
    }

    /**
     * @description 用户登录
     * @author CyberCaelum
     * @date 下午8:48 2025/10/20
     * @param userLoginDTO 电话号，密码
     * @return org.cybercaelum.household_management.pojo.vo.UserLoginVO
     **/
    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        String phoneNumber = userLoginDTO.getPhoneNumber();
        String password = userLoginDTO.getPassword();
        User user = userMapper.getByPhoneNumber(phoneNumber);
        //判断账号是否存在，以及账号状态
        if (user == null||user.getStatus() == StatusConstant.DISABLE) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        //对密码加密，并判断密码是否正确
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(user.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        return newLogin(user);
    }

    /**
     * @description 用户注册
     * @author CyberCaelum
     * @date 下午8:49 2025/10/20
     * @param userRegisterDTO 用户名，密码，电话号
     * @return org.cybercaelum.household_management.pojo.vo.UserLoginVO
     **/
    @Override
    public UserLoginVO register(UserRegisterDTO userRegisterDTO) {
        String password = userRegisterDTO.getPassword();
        String phoneNumber = userRegisterDTO.getPhoneNumber();
        String username = userRegisterDTO.getUsername();
        User user = userMapper.getByUserName(username);
        if (user != null){
            //用户名已存在
            throw new UsernameExistException(MessageConstant.USERNAME_EXIST);
        }
        user = userMapper.getByPhoneNumber(phoneNumber);
        if (user != null){
            if (user.getStatus() == StatusConstant.ENABLE){//账号在使用
                //电话号已使用
                throw new PhoneNumberUsedException(MessageConstant.PHONE_NUMBER_USED);
            }
        }

        //加密密码
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        User user1 = User.builder()
                .password(password)
                .profileUrl("111")
                .username(username)
                .role(RoleConstant.ROLE_USER)
                .status(StatusConstant.ENABLE)
                .createTime(LocalDateTime.now())
                .phoneNumber(phoneNumber)
                .build();
        //新用户注
        userMapper.insertNewUser(user1);
        return newLogin(user1);
    }

    /**
     * @description 更新用户信息
     * @author CyberCaelum
     * @date 下午8:06 2025/10/23
     * @param userUpdateDTO id,手机号，用户名，密码，头像
     * @return org.cybercaelum.household_management.pojo.vo.UserLoginVO
     **/
    @Override
    public UserLoginVO updateUser(UserUpdateDTO userUpdateDTO) {
        User user = new User();
        BeanUtils.copyProperties(userUpdateDTO, user);
        userMapper.updateUser(user);
        return newLogin(user);
    }

    /**
     * @description 用户注销
     * @author CyberCaelum
     * @date 下午8:07 2025/10/23
     **/
    @Override
    public void cancel() {
        //设置用户状态为注销
        userMapper.deleteUserById(BaseContext.getUserId());
        //设置用户发过的招聘为不可见
        //删除线程的信息
    }


}
