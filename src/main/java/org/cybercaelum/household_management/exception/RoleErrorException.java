package org.cybercaelum.household_management.exception;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 权限错误
 * @date 2026/3/15
 */
public class RoleErrorException extends BaseException{
    public RoleErrorException() {}
    public RoleErrorException(String message) {
        super(message);
    }
}
