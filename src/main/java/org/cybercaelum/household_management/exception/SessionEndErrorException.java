package org.cybercaelum.household_management.exception;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 结束会话错误
 * @date 2026/4/1
 */
public class SessionEndErrorException extends BaseException {
    public SessionEndErrorException() {}
    public SessionEndErrorException(String message) {
        super(message);
    }
}
