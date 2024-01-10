package com.example.garbageCollection.service;

import com.example.garbageCollection.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yuyu
 * @since 2023-12-19
 */
public interface IMenuService extends IService<Menu> {
    List<Menu> getAllMenu();
}
