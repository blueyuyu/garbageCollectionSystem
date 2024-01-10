package com.example.garbageCollection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.garbageCollection.entity.Menu;
import com.example.garbageCollection.mapper.MenuMapper;
import com.example.garbageCollection.service.IMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yuyu
 * @since 2023-12-19
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {
    @Override
    public List<Menu> getAllMenu() {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        // 查询出所有的数据
        List<Menu> lists =  list(queryWrapper);
        // 找出pid为null的一级菜单
        List<Menu>  parantNode = lists.stream().filter(menu -> menu.getPid() == null).collect(Collectors.toList());

        // 找出一级菜单的子菜单
        for (Menu i : parantNode){
            i.setChildren(lists.stream().filter(menu -> menu.getPid()== i.getId()).collect(Collectors.toList()));
        }
        return parantNode;
    }
}
