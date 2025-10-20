package org.cybercaelum.household_management.exception;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 业务异常
 * @date 2025/10/20 下午7:58
 */
public class BaseException extends RuntimeException {
    public BaseException(){}
    public BaseException(String message){super(message);}
}
