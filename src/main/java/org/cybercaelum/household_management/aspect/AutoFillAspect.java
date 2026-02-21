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
import java.util.Collection;

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

    //切入点
    @Pointcut("execution(* org.cybercaelum.household_management.mapper.*.*(..)) && @annotation(org.cybercaelum.household_management.annotation.AutoFill)")
    public void autoFillPointCut() {}

    /**
     * @description 前置通知
     * @author CyberCaelum
     * @date 2026/2/21
     * @param joinPoint 切面
     **/
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("公共字段自动填充开始");

        // 获取方法上的注解及操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        // 准备时间数据
        LocalDateTime now = LocalDateTime.now();

        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }

        // 遍历所有参数，处理每个可能包含实体的对象
        for (Object arg : args) {
            processArgument(arg, operationType, now);
        }

        log.info("公共字段自动填充完成");
    }

    /**
     * @description 处理单个参数
     * @author CyberCaelum
     * @date 2026/2/21
     * @param arg 对象
     * @param operationType 操作类型
     * @param now 时间
     **/
    private void processArgument(Object arg, OperationType operationType, LocalDateTime now) {
        if (arg == null) {
            return;
        }

        // 处理集合类型（List、Set 等）
        if (arg instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) arg;
            for (Object item : collection) {
                fillEntity(item, operationType, now);
            }
        }
        // 处理数组类型
        else if (arg.getClass().isArray()) {
            Object[] array = (Object[]) arg;
            for (Object item : array) {
                fillEntity(item, operationType, now);
            }
        }
        // 处理单个实体对象
        else {
            fillEntity(arg, operationType, now);
        }
    }

    /**
     * @description 填充单个实体对象的公共字段
     * @author CyberCaelum
     * @date 2026/2/21
     * @param entity 对象
     * @param operationType 操作类型
     * @param now 时间
     **/
    private void fillEntity(Object entity, OperationType operationType, LocalDateTime now) {
        if (entity == null) {
            return;
        }

        try {
            if (operationType == OperationType.INSERT) {
                // 尝试设置 createTime 和 updateTime
                invokeMethodIfExists(entity, AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class, now);
                invokeMethodIfExists(entity, AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class, now);
            } else if (operationType == OperationType.UPDATE) {
                // 仅设置 updateTime
                invokeMethodIfExists(entity, AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class, now);
            }
        } catch (Exception e) {
            // 如果反射调用失败，记录日志但继续执行，避免中断整个请求
            log.warn("自动填充字段时发生异常，实体类：{}，操作类型：{}，错误信息：{}",
                    entity.getClass().getSimpleName(), operationType, e.getMessage());
        }
    }

    /**
     * @description 如果目标对象存在指定方法，则调用；否则忽略
     * @author CyberCaelum
     * @date 2026/2/21
     * @param target 对象
     * @param methodName 方法名
     * @param paramType 操作类型
     * @param value 参数
     **/
    private void invokeMethodIfExists(Object target, String methodName, Class<?> paramType, Object value) {
        try {
            Method method = target.getClass().getDeclaredMethod(methodName, paramType);
            method.setAccessible(true); // 如果方法是 private，允许访问
            method.invoke(target, value);
        } catch (NoSuchMethodException e) {
            // 方法不存在，说明该对象不是需要填充的实体类，忽略
            log.debug("对象 {} 没有方法 {}，跳过", target.getClass().getSimpleName(), methodName);
        } catch (Exception e) {
            // 其他反射异常（如 IllegalAccessException, InvocationTargetException）
            log.error("调用方法 {}.{} 失败: {}", target.getClass().getSimpleName(), methodName, e.getMessage());
        }
    }
}
