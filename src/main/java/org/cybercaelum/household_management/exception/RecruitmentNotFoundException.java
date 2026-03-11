package org.cybercaelum.household_management.exception;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 招募不存在
 * @date 2026/3/11 下午2:26
 */
public class RecruitmentNotFoundException extends BaseException{
    public RecruitmentNotFoundException() {}
    public RecruitmentNotFoundException(String message) {
        super(message);
    }
}
