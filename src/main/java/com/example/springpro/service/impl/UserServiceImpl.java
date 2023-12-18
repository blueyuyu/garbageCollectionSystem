package com.example.springpro.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springpro.common.Constants;
import com.example.springpro.common.Result;
import com.example.springpro.controller.dto.UserDTO;
import com.example.springpro.entity.User;
import com.example.springpro.exception.ServiceException;
import com.example.springpro.mapper.UserMapper;
import com.example.springpro.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springpro.utils.TokenUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yuyu
 * @since 2023-11-24
 */
/**
 * 实现类,对sql,进行处理
 * */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    // 定义一个变量接收，通过hutool里的方法，打印错误
    private static final Log LOG = Log.get();

    @Override
    public UserDTO login(UserDTO userDTO) {
        User one = getUserInfo(userDTO);
        if(one != null){
            // 有数据直接返回
            // 类型不同，但是 userDTO 在 User类里面
            // 借助了一个方法， BeanUtil.copy
            // 将源对象 source 的属性值复制到目标对象 target 中。这个方法会自动匹配属性名字相同的字段
            // ，并将值从源对象拷贝到目标对象。
            // one 查找到的一些数据，赋值给 userDTO;
            // 在此设置token
            BeanUtil.copyProperties(one,userDTO,true);
            String token =  TokenUtils.createToken(one.getId().toString(),one.getPassword());
            userDTO.setToken(token);
            return userDTO;
        }else{
            // 为空，抛异常，输入错误,
            System.out.println("进入");
            throw new ServiceException(Constants.CODE_1001,"账号或密码错误");
        }
    }

    @Override
    public User register(UserDTO userDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",userDTO.getUsername());
        User namerep;
        try {
            namerep = getOne(queryWrapper);
        } catch (Exception e){
            throw new ServiceException(Constants.CODE_500,"数据库异常");
        }

        if(namerep != null){
            throw new ServiceException(Constants.CODE_1002,"用户名重复");
        }
        User one = getUserInfo(userDTO);
        User user = new User();
        if(one == null){
            // 无重复元素
            BeanUtil.copyProperties(userDTO,user,true);
            save(user);
        }
        return user;
    }

//    封装出一个函数
    public User getUserInfo(UserDTO userDTO){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",userDTO.getUsername());
        queryWrapper.eq("password",userDTO.getPassword());
        User one;
        try{
            // 能找到就返回true
            one = getOne(queryWrapper); // 如果是代码的sql错误，就直接抛出系统异常，如果是代码业务错误才接着处理
        } catch (Exception e){
            // 不能就抛出异常，返回false
            LOG.error(e);
            System.out.println("paochu");
            throw new ServiceException(Constants.CODE_500,"系统错误");
        }
        return one;
    }

}
