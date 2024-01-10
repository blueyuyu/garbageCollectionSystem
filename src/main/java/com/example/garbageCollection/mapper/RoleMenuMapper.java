package com.example.garbageCollection.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.garbageCollection.entity.RoleMenu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    @Select("select menu_id from system_role_menu where role_id=#{roleId }")
    List<Integer> selectByRoleId(@Param("roleId") Integer roleId);
}
