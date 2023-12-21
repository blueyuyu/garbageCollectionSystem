package com.example.springpro.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import com.example.springpro.common.Result;

import com.example.springpro.service.IRoleService;
import com.example.springpro.entity.Role;


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
        roleService.list();
        return Result.success();
    }

    //Role
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        roleService.getById(id);
        return Result.success();
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
}

