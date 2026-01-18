package org.cybercaelum.household_management.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.exception.BaseException;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 全局异常处理器
 * @date 2025/11/9 下午4:01
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * @description 参数异常
     * @author CyberCaelum
     * @date 下午4:34 2025/11/9
     * @param ex 参数异常
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors()
                .stream()
                .map(error -> {
                    if (error instanceof FieldError) {
                        FieldError fieldError = (FieldError) error;
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.joining("; "));

        log.error("参数验证异常: {}", errorMessage);
        return Result.error("验证失败: " + errorMessage);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result handleConstraintViolationException(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations()
                .stream()
                .map(violation -> {
                    String path = violation.getPropertyPath().toString();
                    // 提取方法参数名或字段名
                    String[] pathParts = path.split("\\.");
                    String fieldName = pathParts[pathParts.length - 1];
                    return fieldName + ": " + violation.getMessage();
                })
                .collect(Collectors.joining("; "));

        log.error("约束违反异常: {}", errorMessage);
        return Result.error("参数错误: " + errorMessage);
    }

    /**
     * @description 业务异常
     * @author CyberCaelum
     * @date 下午4:34 2025/11/9
     * @param ex 业务异常
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @ExceptionHandler(BaseException.class)
    public Result handleBaseException(BaseException ex) {
        log.error("业务异常: {}", ex.getMessage());
        return Result.error(ex.getMessage());
    }
}
