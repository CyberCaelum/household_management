package org.cybercaelum.household_management.exception;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 账号不存在
 * @date 2025/10/20 下午7:57
 */
public class AccountNotFoundException extends BaseException {
    public AccountNotFoundException() {}
    public AccountNotFoundException(String message) {super(message);}
}
