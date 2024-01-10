package com.example.garbageCollection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.garbageCollection.common.Constants;
import com.example.garbageCollection.common.RoleEnum;
import com.example.garbageCollection.controller.dto.UserDTO;
import com.example.garbageCollection.entity.Menu;
import com.example.garbageCollection.entity.User;
import com.example.garbageCollection.exception.ServiceException;
import com.example.garbageCollection.mapper.RoleMapper;
import com.example.garbageCollection.mapper.RoleMenuMapper;
import com.example.garbageCollection.mapper.UserMapper;
import com.example.garbageCollection.service.IMenuService;
import com.example.garbageCollection.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.garbageCollection.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private IMenuService menuService;

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

            try {
                // 登陆时获取用户的角色菜单返回；
                String userRole =  one.getRole(); // TOP_ADMIN]
                // 查找role表的id
                Integer roleId =  roleMapper.getIdByRole(userRole);
                // 获取到menu_id 的数组 [1,2,3]
                List<Integer> menuIdList =  roleMenuMapper.selectByRoleId(roleId);
                // 在获取到到全部menu路由
                List<Menu> menus =  menuService.getAllMenu();
                // 根据menuList 去筛选最后获得menu router 返回给用户；
                List<Menu> userMenu = new ArrayList<>();

                for (Menu menu : menus){
                    if(menuIdList.contains(menu.getId())){
                        userMenu.add(menu);
                    }
                    // 子路由的处理
                    menu.getChildren().removeIf(child -> !menuIdList.contains(child.getId()));
                }
                userDTO.setMenuList(userMenu);
            } catch (Exception e) {
                e.printStackTrace();
            }

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
