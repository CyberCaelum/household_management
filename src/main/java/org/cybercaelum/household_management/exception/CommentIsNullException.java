package org.cybercaelum.household_management.exception;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 评论不存在
 * @date 2026/3/15
 */
public class CommentIsNullException extends BaseException{
    public CommentIsNullException() {}
    public CommentIsNullException(String message) {
        super(message);
    }
}
