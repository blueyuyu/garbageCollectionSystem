package com.example.garbageCollection.controller.dto;

import com.example.garbageCollection.entity.Menu;
import lombok.Data;

import java.util.List;

/**
 * 这是登录的user类
 * **/
@Data
public class UserDTO {
    private String username;
    private String password;
    private String nickname;
    private String avatarUrl;
    private String token; // 返回用户token
    private List<Menu> menuList; // 返回管理员用户的菜单
}
