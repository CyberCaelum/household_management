package org.cybercaelum.household_management.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.cybercaelum.household_management.annotation.AutoFill;
import org.cybercaelum.household_management.enumeration.OperationType;
import org.cybercaelum.household_management.pojo.dto.GroupCreateDTO;
import org.cybercaelum.household_management.pojo.entity.OpenimGroup;

@Mapper
public interface OpenimGroupMapper {

    /**
     * @description 创建新群组聊天
     * @author CyberCaelum
     * @date 2026/3/25
     * @param openimGroup 聊天信息
     **/
    @AutoFill(OperationType.INSERT)
    void insertGroup(OpenimGroup openimGroup);

    /**
     * @description 查找聊天群组是否存在
     * @author CyberCaelum
     * @date 2026/3/25
     * @param groupCreateDTO 群组信息
     **/
    @Select("select * from openim_group where recruitment_id = #{recruitmentId} and employee_id = #{initiator} and employer_id = #{accepter} and group_type = #{groupType}")
    OpenimGroup groupIsExist(GroupCreateDTO groupCreateDTO);
}
