package org.cybercaelum.household_management.exception;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 用户不存在
 * @date 2026/3/25
 */
public class UserNotFoundException extends BaseException{
    public UserNotFoundException() {}
    public UserNotFoundException(String message) {
        super(message);
    }
}
