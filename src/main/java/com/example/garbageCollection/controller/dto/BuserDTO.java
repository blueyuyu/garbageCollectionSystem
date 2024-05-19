package com.example.garbageCollection.controller.dto;

import com.example.garbageCollection.entity.Menu;
import lombok.Data;

import java.util.List;

@Data
public class BuserDTO {
    private String id;
    private String username;
    private String password;
    private String nickname;
    private String avatarUrl;
    private String gender; // new
    private String introduction; // new
    private String token; // 返回用户token
}

