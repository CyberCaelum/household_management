package org.cybercaelum.household_management.exception;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 群组创建错误
 * @date 2026/3/30
 */
public class GroupCreateErrorException extends BaseException{
    public GroupCreateErrorException() {}
    public GroupCreateErrorException(String message) {
        super(message);
    }
}
