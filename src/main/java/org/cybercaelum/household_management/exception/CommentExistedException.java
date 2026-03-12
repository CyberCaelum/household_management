package org.cybercaelum.household_management.exception;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 评论已存在
 * @date 2026/3/12
 */
public class CommentExistedException extends BaseException{
    public CommentExistedException() {}
    public CommentExistedException(String message) {
        super(message);
    }
}
