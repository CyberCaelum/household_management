package org.cybercaelum.household_management.exception;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 图片数量超过限制异常
 * @date 2026/2/21
 */
public class PictureCountLimitException extends BaseException {
    public PictureCountLimitException() {}
    public PictureCountLimitException(String message) {super(message);}
}
