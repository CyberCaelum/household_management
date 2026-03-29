package org.cybercaelum.household_management.service.impl;

import org.cybercaelum.household_management.pojo.dto.OpenimUserCallbackDTO;
import org.cybercaelum.household_management.pojo.vo.OpenimCallbackVO;
import org.cybercaelum.household_management.service.OpenimUserCallBackService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: openim用户相关回调服务类
 * @date 2026/3/28
 */
@Service
public class OpenimUserCallBackServiceImpl implements OpenimUserCallBackService {

    private static RedisTemplate<String,Object> redisTemplate;

    @Override
    public OpenimCallbackVO afterOnline(OpenimUserCallbackDTO userCallbackDTO) {

        return null;
    }
}
