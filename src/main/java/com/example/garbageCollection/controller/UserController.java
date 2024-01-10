package com.example.garbageCollection.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.garbageCollection.common.Constants;
import com.example.garbageCollection.common.Result;
import com.example.garbageCollection.controller.dto.UserDTO;
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
    public Boolean save(@RequestBody User user) {
        return userService.saveOrUpdate(user);
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return userService.removeById(id);
    }

    // 使用post删除
    @PostMapping("/del/batch")
    public boolean deleteBatch(@RequestBody List<Integer> ids) {
        return userService.removeByIds(ids);
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
    public Page<User> findPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize, @RequestParam(defaultValue = "") String username,
                               @RequestParam(defaultValue = "") String nickname,
                               @RequestParam(defaultValue = "") String address) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(username)) {
            queryWrapper.like("username", username);
        }
        if (!"".equals(nickname)) {
            queryWrapper.like("nickname", nickname);
        }
        if (!"".equals(address)) {
            queryWrapper.like("address", address);
        }
//        User u =  TokenUtils.getUserInfo();
//        System.out.println(u.getNickname());
        return userService.page(new Page<>(pageNum, pageSize));
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

//        writer.addHeaderAlias("username", "用户名");
//        writer.addHeaderAlias("password", "密码");
//        writer.addHeaderAlias("nickname", "昵称");
//        writer.addHeaderAlias("email", "邮箱");
//        writer.addHeaderAlias("phone", "电话");
//        writer.addHeaderAlias("address", "地址");
//        writer.addHeaderAlias("createTime", "创建时间");
//        writer.addHeaderAlias("avatarUrl", "头像");
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

