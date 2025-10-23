package org.cybercaelum.household_management.controller.user;

import lombok.RequiredArgsConstructor;
import org.cybercaelum.household_management.service.CommonService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 通用接口
 * @date 2025/10/23 下午7:05
 */
@RequestMapping
@RestController
@RequiredArgsConstructor
public class CommonController {
    private final CommonService commonService;
}
