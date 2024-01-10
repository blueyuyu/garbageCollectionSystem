package com.example.garbageCollection.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("system_role_menu")
public class RoleMenu {
    private Integer role_id;
    private Integer menu_id;
}
