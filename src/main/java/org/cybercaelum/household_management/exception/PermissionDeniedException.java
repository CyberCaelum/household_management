package org.cybercaelum.household_management.exception;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 权限不足异常
 * @date 2026/3/15
 */
public class PermissionDeniedException extends BaseException {
    public PermissionDeniedException(String message) {
        super(message);
    }
    public PermissionDeniedException() {}
}
