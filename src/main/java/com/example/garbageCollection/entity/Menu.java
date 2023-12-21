package com.example.springpro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author yuyu
 * @since 2023-12-19
 */
@Getter
@Setter
  @TableName("system_menu")
@ApiModel(value = "Menu对象", description = "")
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("id")
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("菜单名称")
      private String name;

      @ApiModelProperty("菜单路径")
      private String path;

      @ApiModelProperty("菜单图标")
      private String icon;

      @ApiModelProperty("菜单描述")
      private String description;

      // 菜单子节点，在·表中并不存在
      @TableField(exist = false)
      private List<Menu> children;

      private Integer pid;
}
