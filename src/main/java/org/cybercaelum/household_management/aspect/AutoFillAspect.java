package org.cybercaelum.household_management.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.cybercaelum.household_management.annotation.AutoFill;
import org.cybercaelum.household_management.constant.AutoFillConstant;
import org.cybercaelum.household_management.enumeration.OperationType;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Before;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 自定义切面，实现公共字段自动填充
 * @date 2025/11/10 下午8:26
 */
@Component
@Slf4j
@Aspect
public class AutoFillAspect {
    /**
     * @description 切入点
     * @author CyberCaelum
     * @date 下午8:31 2025/11/10
     **/
    @Pointcut("execution(* org.cybercaelum.household_management.mapper.*.*(..)) && @annotation(org.cybercaelum.household_management.annotation.AutoFill)")
    public void autoFillPointCut() {}

    /**
     * @description 前置通知
     * @author CyberCaelum
     * @date 下午8:35 2025/11/10
     * @param joinPoint 切面
     **/
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("公共字段填充");
        //获取到当前被拦截方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
        OperationType operationType = autoFill.value();//获得数据库操作类型

        //获取到当前被拦截的方法的参数--实体对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        Object arg = args[0];

        //准备赋值数据
        LocalDateTime now = LocalDateTime.now();

        //根据当前不同的操作类型，为对应的属性通过反射来赋值
        if (operationType == OperationType.INSERT) {
            try {
                Method setCreateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);

                //通过反射为对象属性赋值
                setCreateTime.invoke(arg,now);
                setUpdateTime.invoke(arg,now);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else if (operationType == OperationType.UPDATE) {
            try {
                Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                setUpdateTime.invoke(arg,now);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }
}
