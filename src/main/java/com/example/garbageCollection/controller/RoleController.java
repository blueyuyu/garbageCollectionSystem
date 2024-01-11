package com.example.garbageCollection.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.garbageCollection.entity.Menu;
import com.example.garbageCollection.service.IMenuService;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import com.example.garbageCollection.common.Result;

import com.example.garbageCollection.service.IRoleService;
import com.example.garbageCollection.entity.Role;


import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yuyu
 * @since 2023-12-19
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    private IRoleService roleService;

    @Resource
    private IMenuService menuService;


    @PostMapping
    public Result save(@RequestBody Role role) {
         roleService.saveOrUpdate(role);
         return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
         roleService.removeById(id);
        return Result.success();
    }

    // 使用post删除
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
         roleService.removeByIds(ids);
         return Result.success();
    }

    // List<Role>
    @GetMapping
    public Result findAll() {
        return Result.success(roleService.list());
    }

    //获取Role
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(roleService.getById(id));
    }

    //Page<Role>
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
    @RequestParam Integer pageSize) {
            QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("id");
            roleService.page(new Page<>(pageNum, pageSize));
            return Result.success();
    }

    // 给角色分配路由表，
    // 当选择子路由的时候，也要选中他的父级路由
    @PostMapping("/roleMenu/{id}")
    public Result updateRoleMenu(@PathVariable Integer id, @RequestBody ArrayList<Integer> menulist) {
        try {
            // 检查角色
            Role isExistRole = roleService.getById(id);
            if(isExistRole == null){
                return Result.error("400","角色不存在");
            }

            // 检查菜单是否存在
            for (int menuid : menulist ){
                Menu isExistMenu = menuService.getById(menuid);
                if(isExistMenu == null){
                    return Result.error("400","菜单id传入错误");
                }
            }
            // 检查菜单
            roleService.setRoleMenu(id,menulist);
            return Result.success("权限分配成功");
        }catch (Exception e){
            throw e;
        }

    }

    // getRoleMenu
    @GetMapping("/roleMenu/{roleId}")
    public Result getRoleMenu(@PathVariable Integer roleId ){
        return Result.success(roleService.getRoleMenu(roleId));
    }

}

