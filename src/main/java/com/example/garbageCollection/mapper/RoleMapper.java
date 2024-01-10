package com.example.garbageCollection.mapper;

import com.example.garbageCollection.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yuyu
 * @since 2023-12-19
 */
public interface RoleMapper extends BaseMapper<Role> {

    @Select("select id from system_role where role_key=#{role_key}")
    Integer getIdByRole(@Param("role_key") String role_key);
}
