package org.cybercaelum.household_management.exception;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: openim服务请求错误
 * @date 2026/3/23
 */
public class OpenimRequestErrorException extends BaseException{
    public OpenimRequestErrorException() {}
    public OpenimRequestErrorException(String message) {
        super(message);
    }
}
