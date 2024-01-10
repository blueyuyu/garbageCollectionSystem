package com.example.garbageCollection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.garbageCollection.entity.Role;
import com.example.garbageCollection.entity.RoleMenu;
import com.example.garbageCollection.mapper.RoleMapper;
import com.example.garbageCollection.mapper.RoleMenuMapper;
import com.example.garbageCollection.service.IRoleMenuService;
import com.example.garbageCollection.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yuyu
 * @since 2023-12-19
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {
    @Resource
    private RoleMenuMapper roleMenuMapper;

    // transactional 某个操作抛出运行时异常，整个事务将回滚；
    @Transactional
    @Override
    public void setRoleMenu(Integer id, ArrayList<Integer> menulist) {
        // 实现逻辑，先删后增
        QueryWrapper<RoleMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id",id);
        roleMenuMapper.delete(queryWrapper);

        // 然后添加,逐个添加
        for (int menuId : menulist){
            RoleMenu roleMenu =  new RoleMenu();
            roleMenu.setRole_id(id);
            roleMenu.setMenu_id(menuId);
            roleMenuMapper.insert(roleMenu);
        }

    }

    @Override
    public List<Integer> getRoleMenu(Integer roleId) {
        return roleMenuMapper.selectByRoleId(roleId);
    }
}
