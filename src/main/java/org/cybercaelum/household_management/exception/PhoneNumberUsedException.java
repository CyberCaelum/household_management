package org.cybercaelum.household_management.exception;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 电话号已使用
 * @date 2025/10/20 下午8:55
 */
public class PhoneNumberUsedException extends BaseException{
    public PhoneNumberUsedException() {}
    public PhoneNumberUsedException(String message) {super(message);}
}
