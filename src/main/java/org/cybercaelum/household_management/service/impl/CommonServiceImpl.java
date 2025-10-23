package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.service.CommonService;
import org.cybercaelum.household_management.utils.AliOssUtil;
import org.springframework.stereotype.Service;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 通用接口服务层
 * @date 2025/10/23 下午8:57
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {
    private final AliOssUtil aliOssUtil;

}
