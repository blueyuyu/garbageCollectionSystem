package com.example.springpro.service;

import com.example.springpro.controller.dto.UserDTO;
import com.example.springpro.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yuyu
 * @since 2023-11-24
 */
/**
 * 接口处，创建方法
 ***/
public interface IUserService extends IService<User> {

    UserDTO login(UserDTO userDTO);

    User register(UserDTO userDTO);
}
