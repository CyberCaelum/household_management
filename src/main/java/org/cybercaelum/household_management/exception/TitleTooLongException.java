package org.cybercaelum.household_management.exception;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 标题过长
 * @date 2025/11/9 下午2:40
 */
public class TitleTooLongException extends BaseException{
    public TitleTooLongException() {}
    public TitleTooLongException(String message) {super(message);}
}
