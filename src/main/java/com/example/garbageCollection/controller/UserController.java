package com.example.garbageCollection.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.garbageCollection.common.Constants;
import com.example.garbageCollection.common.Result;
import com.example.garbageCollection.controller.dto.UserDTO;
import com.example.garbageCollection.mapper.UserMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

import com.example.garbageCollection.service.IUserService;
import com.example.garbageCollection.entity.User;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author yuyu
 * @since 2023-11-24
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private IUserService userService;
    @Resource
    private UserMapper userMapper;

    // 用户登录方法
    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO){
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if(StrUtil.isBlank(username) || StrUtil.isBlank(password) ){
            return Result.error(Constants.CODE_400,"参数错误");
        }
        UserDTO user =  userService.login(userDTO);
        return Result.success(user);
    }

    // 用户注册
    @PostMapping("/register")
    public Result register(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if(StrUtil.isBlank(username) || StrUtil.isBlank(password) ){
            return Result.error(Constants.CODE_400,"用户名或密码不能为空");
        }
        return Result.success(userService.register(userDTO));
    }

    // 用户更新
    @PostMapping
    public Result save(@RequestBody User user) {
        // 这个应该是 userid 不存在的情况, 只能通过用户名更新
        if(user.getId() == null){
            // 根据用户名查找到id ,再将id 赋值上去，然后进行更新操作
            // 这里应该使用包装类型 Integer 否则会抛出异常
            Integer userId = userMapper.findUserIdByUsername(user.getUsername());
            if(userId != null){
                user.setId(userId);
            }
        }
        boolean result = userService.saveOrUpdate(user);
        if(result){
            return Result.success(null,"更新用户信息成功");
        }else{
            return Result.error("1000","更新用户失败");
        }
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return userService.removeById(id);
    }

    // 使用post删除
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(userService.removeByIds(ids),"批量删除成功");
    }

    @GetMapping
    public List<User> findAll() {
        return userService.list();
    }

    @GetMapping("/{id}")
    public User findOne(@PathVariable Integer id) {
        return userService.getById(id);
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "") String username,
                               @RequestParam(defaultValue = "") String email,
                               @RequestParam(defaultValue = "") String address) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("id");
        if (!"".equals(username)) {
            queryWrapper.like("username", username);
        }
        if (!"".equals(email)) {
            queryWrapper.like("email", email);
        }
        if (!"".equals(address)) {
            queryWrapper.like("address", address);
        }
//        User u =  TokenUtils.getUserInfo();
//        System.out.println(u.getNickname());
        return Result.success(userService.page(new Page<>(pageNum, pageSize),queryWrapper));
    }

    //    excell 导出接口
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 查出所有的数据
        List<User> list = userService.list();
        // 通过工具类创建writer，默认创建xls格式
        ExcelWriter writer = ExcelUtil.getWriter(true);
//创建xlsx格式的
//ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.addHeaderAlias("id", "id");
        writer.addHeaderAlias("username", "用户名");
        writer.addHeaderAlias("password", "密码");
        writer.addHeaderAlias("nickname", "昵称");
        writer.addHeaderAlias("email", "邮箱");
        writer.addHeaderAlias("phone", "电话");
        writer.addHeaderAlias("address", "地址");
        writer.addHeaderAlias("createTime", "创建时间");
        writer.addHeaderAlias("avatarUrl", "头像");
        writer.addHeaderAlias("role", "权限");

// 一次性写出内容，使用默认样式，强制输出标题
        writer.write(list,true);

//        设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息","UTF-8");
        response.setHeader("Content-Disposition","attachment;filename="+fileName+".xlsx");
        ServletOutputStream out=response.getOutputStream();
//out为OutputStream，需要写出到的目标流
        writer.flush(out,true);
// 关闭writer，释放内存
        out.close();
        writer.close();
        //
    }

    /**
     * @post
     * params file
     * 导入 excell 表格的形式
     * **/
    @PostMapping("/import")
    public boolean impfile(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        List<User> all = reader.readAll(User.class);
        // 批量插入数据库
       userService.saveBatch(all);
       return true;
    }

}

