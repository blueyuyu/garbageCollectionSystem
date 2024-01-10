package com.example.garbageCollection.service;

import com.example.garbageCollection.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.garbageCollection.entity.RoleMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yuyu
 * @since 2023-12-19
 */
public interface IRoleService extends IService<Role> {

    void setRoleMenu(Integer id, ArrayList<Integer> menulist);

    List<Integer> getRoleMenu(Integer roleId);
}
