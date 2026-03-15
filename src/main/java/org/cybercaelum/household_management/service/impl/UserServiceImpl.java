package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.cybercaelum.household_management.pojo.entity.User;
import org.cybercaelum.household_management.pojo.vo.UserLoginVO;
import org.cybercaelum.household_management.properties.JwtProperties;
import org.cybercaelum.household_management.service.OpenImService;
import org.cybercaelum.household_management.service.UserService;
import org.cybercaelum.household_management.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

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
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtProperties jwtProperties;
    private final OpenImService openImService;

    /**
     * @description 创建token并获取 OpenIM token
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
        
        // 获取 OpenIM token
        String openImToken = null;
        try {
            String userIdStr = String.valueOf(user.getId());
            openImToken = openImService.getUserToken(userIdStr);
        } catch (Exception e) {
            log.error("获取 OpenIM token 失败, userId={}", user.getId(), e);
            // 不影响主流程，继续返回登录结果
        }
        
        return UserLoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .profileUrl(user.getProfileUrl())
                .token(token)
                .openImToken(openImToken)
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
     * @description 用户注册（同时注册到 OpenIM）
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
                .role(RoleConstant.USER)
                .status(StatusConstant.ENABLE)
                .createTime(LocalDateTime.now())
                .phoneNumber(phoneNumber)
                .build();
        //新用户注册
        userMapper.insertNewUser(user1);
        
        // 注册到 OpenIM（使用生成的用户ID）
        try {
            String userIdStr = String.valueOf(user1.getId());
            openImService.registerUser(userIdStr, username, user1.getProfileUrl());
        } catch (Exception e) {
            log.error("OpenIM 用户注册失败, userId={}", user1.getId(), e);
            // 记录日志，但不影响主流程
        }
        
        return newLogin(user1);
    }

    /**
     * @description 更新用户信息（同时更新 OpenIM）
     * @author CyberCaelum
     * @date 下午8:06 2025/10/23
     * @param userUpdateDTO id,手机号，用户名，密码，头像
     * @return org.cybercaelum.household_management.pojo.vo.UserLoginVO
     **/
    @Override
    public UserLoginVO updateUser(UserUpdateDTO userUpdateDTO) {
        User user = new User();
        String password = userUpdateDTO.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        BeanUtils.copyProperties(userUpdateDTO, user);
        user.setPassword(password);
        userMapper.updateUser(user);
        
        // 同步更新 OpenIM 用户信息
        try {
            String userIdStr = String.valueOf(user.getId());
            openImService.updateUserInfo(userIdStr, user.getUsername(), user.getProfileUrl());
        } catch (Exception e) {
            log.error("OpenIM 更新用户信息失败, userId={}", user.getId(), e);
            // 记录日志，但不影响主流程
        }
        
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
