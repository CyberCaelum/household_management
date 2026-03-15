package org.cybercaelum.household_management.exception;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 订单不存在异常
 * @date 2026/3/15
 */
public class OrderNotFoundException extends BaseException {
    public OrderNotFoundException(String message) {
        super(message);
    }
    public OrderNotFoundException() {}
}
