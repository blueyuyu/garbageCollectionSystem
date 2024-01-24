package com.example.garbageCollection.mapper;

import com.example.garbageCollection.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yuyu
 * @since 2023-11-24
 */
public interface UserMapper extends BaseMapper<User> {
    @Select("select id from system_user where username=#{ username }")
    Integer findUserIdByUsername(@Param("username") String username);
}
