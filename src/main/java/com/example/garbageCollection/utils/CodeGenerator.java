package com.example.garbageCollection.utils;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;

import java.util.Collections;

/**
 * 代码生成器类
 * */
public class CodeGenerator {
    public static void main(String[] args) {
        generate();
    }

    private static void generate(){
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/goldfish?serverTimezone=GMT%2b8", "root", "admin123")
                .globalConfig(builder -> {
                    builder.author("yuyu") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("E:\\lajiPro\\garbageCollectionSystem\\src\\main\\java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.example.garbageCollection") // 设置父包名
                            .moduleName(null) // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "E:\\lajiPro\\garbageCollectionSystem\\src\\main\\resources\\mapper")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.entityBuilder().enableLombok().enableRemoveIsPrefix();
                    builder.controllerBuilder().enableHyphenStyle()  // 开启驼峰转连字符
                            .enableRestStyle();  // 开启生成@RestController 控制器, 必须加，不加返回不了json
                    builder.addInclude("system_category") // 设置需要生成的表名
                            .addTablePrefix("t_", "c_","system_"); // 设置过滤表前缀
                })
                .execute();
    }
}
