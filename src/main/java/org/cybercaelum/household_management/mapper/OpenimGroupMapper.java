package org.cybercaelum.household_management.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.cybercaelum.household_management.pojo.entity.OpenimGroup;

@Mapper
public interface OpenimGroupMapper {
    void insertGroup(OpenimGroup openimGroup);
}
