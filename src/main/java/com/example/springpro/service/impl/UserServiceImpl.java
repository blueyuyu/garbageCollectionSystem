package com.example.springpro.service.impl;

import com.example.springpro.entity.User;
import com.example.springpro.mapper.UserMapper;
import com.example.springpro.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yuyu
 * @since 2023-11-24
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
