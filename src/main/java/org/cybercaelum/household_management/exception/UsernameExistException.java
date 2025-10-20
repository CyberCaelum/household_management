package org.cybercaelum.household_management.exception;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 用户名已存在
 * @date 2025/10/20 下午9:02
 */
public class UsernameExistException extends BaseException{
    public UsernameExistException() {}
    public UsernameExistException(String message) {super(message);}
}
