package org.cybercaelum.household_management.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.context.BaseContext;
import org.cybercaelum.household_management.pojo.dto.UserLoginDTO;
import org.cybercaelum.household_management.pojo.dto.UserRegisterDTO;
import org.cybercaelum.household_management.pojo.dto.UserUpdateDTO;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.cybercaelum.household_management.pojo.vo.UserLoginVO;
import org.cybercaelum.household_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 用户相关接口
 * @date 2025/10/15 下午8:55
 */

@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "用户服务",description = "用户服务")
public class UserController {

    private final UserService userService;

    /**
     * @description 用户登录
     * @author CyberCaelum
     * @date 下午8:50 2025/10/20
     * @param userLoginDTO 电话号，密码
     * @return org.cybercaelum.household_management.pojo.entity.Result<org.cybercaelum.household_management.pojo.vo.UserLoginVO>
     **/
    @Operation(summary = "用户登录",description = "用户登录获取token")
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录{}",userLoginDTO);
        UserLoginVO userLoginVO = userService.login(userLoginDTO);
        BaseContext.remove();
        return Result.success(userLoginVO);
    }

    /**
     * @description 用户注册
     * @author CyberCaelum
     * @date 下午8:50 2025/10/20
     * @param userRegisterDTO 用户名，密码，电话号
     * @return org.cybercaelum.household_management.pojo.entity.Result<org.cybercaelum.household_management.pojo.vo.UserLoginVO>
     **/
    @Operation(summary = "用户注册",description = "用户注册获取token")
    @PostMapping("/register")
    public Result<UserLoginVO> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        log.info("用户登录{}",userRegisterDTO);
        UserLoginVO userLoginVO = userService.register(userRegisterDTO);
        BaseContext.remove();
        return Result.success(userLoginVO);
    }

    /**
     * @description 用户退出登录
     * @author CyberCaelum
     * @date 下午6:41 2025/10/23
     * @return org.cybercaelum.household_management.pojo.entity.Result<java.lang.String>
     **/
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "退出登录",description = "用户退出登录")
    @PostMapping("/logout")
    public Result<String> logout(){
        log.info("用户退出登录");
        BaseContext.remove();
        return Result.success();
    }

    /**
     * @description 更新用户信息
     * @author CyberCaelum
     * @date 下午7:55 2025/10/23
     * @param userUpdateDTO id,用户名，电话号，密码，头像
     * @return org.cybercaelum.household_management.pojo.entity.Result<org.cybercaelum.household_management.pojo.vo.UserLoginVO>
     **/
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "更新用户信息",description = "用户更新信息")
    @PostMapping("update")
    public Result<UserLoginVO> updateUser(@RequestBody UserUpdateDTO userUpdateDTO){
        log.info("用户更新{}",userUpdateDTO);
        UserLoginVO userLoginVO = userService.updateUser(userUpdateDTO);
        BaseContext.remove();
        return Result.success(userLoginVO);
    }

    /**
     * @description 注销账号
     * @author CyberCaelum
     * @date 下午8:04 2025/10/23
     * @return org.cybercaelum.household_management.pojo.entity.Result<java.lang.String>
     **/
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "注销账号",description = "用户注销")
    @DeleteMapping("/cancel")
    public Result<String> cancel(){
        log.info("用户注销");
        userService.cancel();
        BaseContext.remove();
        return Result.success();
    }
}
