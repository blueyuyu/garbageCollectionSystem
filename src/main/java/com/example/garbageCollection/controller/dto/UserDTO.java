package com.example.springpro.controller.dto;

import lombok.Data;

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
}
