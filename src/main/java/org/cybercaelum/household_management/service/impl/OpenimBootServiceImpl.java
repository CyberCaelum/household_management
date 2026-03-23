package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.exception.OpenimRequestErrorException;
import org.cybercaelum.household_management.feign.OpenimFeignClient;
import org.cybercaelum.household_management.mapper.OpenimBootMapper;
import org.cybercaelum.household_management.pojo.dto.NotificationAccountInfo;
import org.cybercaelum.household_management.pojo.dto.OpenimBootAddDTO;
import org.cybercaelum.household_management.pojo.entity.OpenimBoot;
import org.cybercaelum.household_management.pojo.entity.OpenimResult;
import org.cybercaelum.household_management.service.OpenimBootService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 聊天机器人服务类
 * @date 2026/3/23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenimBootServiceImpl implements OpenimBootService {

    private final OpenimBootMapper openimBootMapper;
    private final OpenimFeignClient openimFeignClient;

    /**
     * @description 新增机器人账号
     * @author CyberCaelum
     * @date 2026/3/23
     * @param openimBootAddDTO 机器人账号信息
     **/
    @Transactional
    @Override
    public void addBoot(OpenimBootAddDTO openimBootAddDTO) {
        //获取信息
        OpenimBoot openimBoot = OpenimBoot.builder()
                .nickName(openimBootAddDTO.getNickName())
                .build();
        if (openimBootAddDTO.getFaceUrl() != null && !openimBootAddDTO.getFaceUrl().isEmpty()) {
            openimBoot.setFaceUrl(openimBootAddDTO.getFaceUrl());
        }
        //请求openim
        OpenimResult<NotificationAccountInfo> openimResult =
                openimFeignClient.addNotificationAccount(null,null,openimBoot);
        //判断请求是否成功
        if (openimResult.getErrCode() != 0){
            throw new OpenimRequestErrorException("创建机器人账号失败");
        }
        OpenimBoot openimBoot2 = new OpenimBoot();
        BeanUtils.copyProperties(openimResult.getData(), openimBoot2);
        openimBoot2.setFaceUrl(openimResult.getData().getFaceURL());
        //存入数据库
        openimBootMapper.addBoot(openimBoot2);
    }
}
