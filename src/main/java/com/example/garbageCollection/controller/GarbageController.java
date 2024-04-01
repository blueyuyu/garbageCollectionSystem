package com.example.garbageCollection.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.example.garbageCollection.service.IGarbageService;
import com.example.garbageCollection.entity.Garbage;

import com.example.garbageCollection.common.Result;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author yuyu
 * @since 2024-01-26
 */
@RestController
@RequestMapping("/garbage")
public class GarbageController {

    @Resource
    private IGarbageService garbageService;

    @PostMapping
    public Result save(@RequestBody Garbage garbage) {
        garbageService.saveOrUpdate(garbage);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        garbageService.removeById(id);
        return Result.success();
    }

    // 使用post删除
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(garbageService.removeByIds(ids), "批量删除成功");
    }

    // List<Garbage>
    @GetMapping
    public Result findAll() {
        garbageService.list();
        return Result.success();
    }

    //Garbage
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        garbageService.getById(id);
        return Result.success();
    }

    //Page<Garbage>
    // 查找垃圾的接口
    @GetMapping("/page")
    @IgnornToken
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize, @RequestParam(defaultValue = "") String name,
                           @RequestParam(defaultValue = "") String category) {
        QueryWrapper<Garbage> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        if (!"".equals(category)) {
            queryWrapper.like("category", category);
        }
        return Result.success(garbageService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    //    excell 导出接口
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 查出所有的数据
        List<Garbage> list = garbageService.list();
        // 通过工具类创建writer，默认创建xls格式
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //创建xlsx格式的
        //ExcelWriter writer = ExcelUtil.getWriter(true);

        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(list, true);

        //        设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("垃圾分类信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        ServletOutputStream out = response.getOutputStream();
        //out为OutputStream，需要写出到的目标流
        writer.flush(out, true);
        // 关闭writer，释放内存
        out.close();
        writer.close();
        //
    }

    /**
     * @get 导出模板
     *
     * **/
    @GetMapping("/exportTemplate")
    public void exportTemplate(HttpServletResponse response) throws Exception{
        ExcelWriter writer = ExcelUtil.getWriter(true);
        // 添加表头，根据实际情况添加列名
        writer.addHeaderAlias("name", "name");
        writer.addHeaderAlias("category", "category");
        List<String> row1 = CollUtil.newArrayList("name","category");
        List<String> row2 = CollUtil.newArrayList("","");
        List<List<String>> rows = CollUtil.newArrayList(row1,row2);
//        ArrayList<Garbage> list = new ArrayList<>();
        // 写出数据为空的 Excel 模板
        writer.write(rows, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("垃圾分类信息模板", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        ServletOutputStream out = response.getOutputStream();

        // 将 Excel 模板写出到输出流
        writer.flush(out, true);

        // 关闭 writer，释放内存
        out.close();
        writer.close();
    }

    /**
     * @post params file
     * 导入 excell 表格的形式
     **/
    @PostMapping("/import")
    public Result impfile(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        List<Garbage> list = reader.readAll(Garbage.class);
        // 批量插入数据库
        garbageService.saveBatch(list);
        return Result.success(null, "导入成功");
    }

}



