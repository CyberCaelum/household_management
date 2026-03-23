package org.cybercaelum.household_management.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.cybercaelum.household_management.pojo.entity.OpenimBoot;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 聊天机器人Mapper
 * @date 2026/3/23
 */
@Mapper
public interface OpenimBootMapper {

    void addBoot(OpenimBoot openimBoot);
}
