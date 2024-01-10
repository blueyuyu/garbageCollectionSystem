package com.example.garbageCollection.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import com.example.garbageCollection.common.Result;

import com.example.garbageCollection.service.IMenuService;
import com.example.garbageCollection.entity.Menu;


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
@RequestMapping("/menu")
public class MenuController {

    @Resource
    private IMenuService menuService;

    @PostMapping
    public Result save(@RequestBody Menu menu) {
         menuService.saveOrUpdate(menu);
         return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
         menuService.removeById(id);
        return Result.success();
    }

    // 使用post删除
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
         menuService.removeByIds(ids);
         return Result.success();
    }

    // List<Menu>
    @GetMapping
    public Result findAll() {
        // 如果查询就要加这个queryWrapper 与 上方name参数
//        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
//        queryWrapper.like("name",name);
//        queryWrapper.orderByDesc("id");

        List<Menu> parentNode =  menuService.getAllMenu();
        return Result.success(parentNode);
    }

    //Menu
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
            menuService.getById(id);
        return Result.success();
    }

    //Page<Menu>
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
    @RequestParam Integer pageSize) {
            QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("id");
            menuService.page(new Page<>(pageNum, pageSize));
            return Result.success();
        }
}

