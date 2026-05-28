package org.cybercaelum.household_management.exception;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 取消请求不存在
 * @date 2026/5/28 上午10:21
 */
public class CancelApplicationIsNullException extends BaseException{
    public CancelApplicationIsNullException() {}
    public CancelApplicationIsNullException(String message) {super(message);}
}
