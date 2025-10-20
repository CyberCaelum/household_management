package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.cybercaelum.household_management.constant.JwtClaimsConstant;
import org.cybercaelum.household_management.constant.MessageConstant;
import org.cybercaelum.household_management.exception.AccountNotFoundException;
import org.cybercaelum.household_management.exception.PasswordErrorException;
import org.cybercaelum.household_management.exception.PhoneNumberUsedException;
import org.cybercaelum.household_management.exception.UsernameExistException;
import org.cybercaelum.household_management.mapper.UserMapper;
import org.cybercaelum.household_management.pojo.dto.UserLoginDTO;
import org.cybercaelum.household_management.pojo.dto.UserRegisterDTO;
import org.cybercaelum.household_management.pojo.entity.User;
import org.cybercaelum.household_management.pojo.vo.UserLoginVO;
import org.cybercaelum.household_management.properties.JwtProperties;
import org.cybercaelum.household_management.service.UserService;
import org.cybercaelum.household_management.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtProperties jwtProperties;

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
        if (user == null||user.getStatus() == 0) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        //对密码加密，并判断密码是否正确
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(user.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
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
        Boolean phoneNumBeUsed = false;
        User user = userMapper.getByPhoneNumber(phoneNumber);
        if (user != null){
            if (user.getStatus() == 1){//账号在使用
                //电话号已使用
                throw new PhoneNumberUsedException(MessageConstant.PHONE_NUMBER_USED);
            }
            //注销账号
            phoneNumBeUsed = true;
        }
        user = userMapper.getByUserName(username);
        if (user != null){
                //用户名已存在
                throw new UsernameExistException(MessageConstant.USERNAME_EXIST);
        }
        //加密密码
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //新用户注册
        if (!phoneNumBeUsed){
            User user1 = User.builder()
                    .password(password)
                    .profileUrl("111")
                    .username(username)
                    .role(1)
                    .status(1)
                    .createTIme(LocalDateTime.now())
                    .phoneNumber(phoneNumber)
                    .build();
            userMapper.insertNewUser(user1);
        }
        //注销账号信息更新
        else {

        }



        return null;
    }
}
