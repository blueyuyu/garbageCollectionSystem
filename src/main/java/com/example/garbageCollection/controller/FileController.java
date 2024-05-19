package com.example.garbageCollection.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.garbageCollection.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

import com.example.garbageCollection.service.IFileService;
import com.example.garbageCollection.entity.Files;

import com.example.garbageCollection.common.Result;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yuyu
 * @since 2024-05-19
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private IFileService fileService;

    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Resource
    private FileMapper fileMapper;

    @PostMapping("/upload")
    public Result upload(@RequestParam MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String type =  FileUtil.extName(originalFilename);
        long size =  file.getSize();
        // 先存储到磁盘
        // 存储到数据库
        File uploadFile = new File(fileUploadPath);
        // 判断配置的文件目录是否存在，不存在就创建新的目录
        if(!uploadFile.exists()){
            uploadFile.mkdirs();
        }
        // 定义一个文件的唯一的标识码
        String uuid = IdUtil.fastSimpleUUID();
        String fileUuid = uuid+ StrUtil.DOT + type;
        File uploadFileLast = new File(fileUploadPath+ fileUuid);

        // 下面主要是完成一个图片去重操作，通过md5 来比对，md5相同的图片不存储
        // 获取文件的md5 , 通过比对，避免重复上传相同内容的文件
        // 获取文件的md5
        String md5;
        String url;
        // 文件上传磁盘
        file.transferTo(uploadFileLast);
        // 获取文件的md5
        md5 = SecureUtil.md5(uploadFileLast);
        // 数据库查询是否存在相同的记录
        Files fileByMd5 = getFileByMd5(md5);
        if(Objects.nonNull(fileByMd5)){
            url = fileByMd5.getUrl();
            // 已经存在就要删除元素
            uploadFileLast.delete();
        }else{
            // 不存在就继续之前操作
            url = "http://localhost:9091/file/"+fileUuid;
        }

        // 存数据库
        Files saveFile = new Files();
        saveFile.setName(originalFilename);
        saveFile.setType(type);
        saveFile.setSize(size);
        saveFile.setUrl(url);
        saveFile.setMd5(md5);
        fileMapper.insert(saveFile);
        return Result.success(url);
    }

    /**
     * 通过文件的md5 查询图片
     * **/
    private Files getFileByMd5(String md5){
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5",md5);
        List<Files> filesList = fileMapper.selectList(queryWrapper);
        return filesList.size() == 0 ? null : filesList.get(0);
    }

    /**
     * 根据路径下载链接
     * **/
    @GetMapping("/{fileUuid}")
    public  Result download(@PathVariable String fileUuid, HttpServletResponse response) throws IOException {
        // 根据文件唯一标识，获取文件
        File uploadFile = new File(fileUploadPath + fileUuid);
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode(fileUuid,"utf-8"));
        response.setContentType("application/octet-stream");

        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
        return Result.success("ok");
    }

    @PostMapping
    public Result save(@RequestBody Files file) {
         return Result.success(fileService.saveOrUpdate(file));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(fileService.removeById(id));
    }

    // 使用post删除
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
         return Result.success(fileService.removeByIds(ids),"批量删除成功");
    }

    // List<File>
//    @GetMapping("/all")
//    public Result findAll() {
//        return Result.success(fileService.list());
//    }

    //File
    @GetMapping("/get/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success( fileService.getById(id));
    }

    //Page<File>
    @GetMapping("/page/page")
    public Result findPage(@RequestParam Integer pageNum,
    @RequestParam Integer pageSize,@RequestParam(defaultValue = "") String type) {
            QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByAsc("id");
            if(!"".equals(type)){
                queryWrapper.like("type",type);
            }
            return Result.success(fileService.page(new Page<>(pageNum, pageSize),queryWrapper));
    }

    //    excell 导出接口
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
            // 查出所有的数据
            List<Files> list = fileService.list();
            // 通过工具类创建writer，默认创建xls格式
            ExcelWriter writer = ExcelUtil.getWriter(true);
    //创建xlsx格式的
    //ExcelWriter writer = ExcelUtil.getWriter(true);

    // 一次性写出内容，使用默认样式，强制输出标题
            writer.write(list,true);

    //        设置浏览器响应的格式
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            String fileName = URLEncoder.encode("垃圾分类信息","UTF-8");
            response.setHeader("Content-Disposition","attachment;filename="+fileName+".xlsx");
            ServletOutputStream out=response.getOutputStream();
    //out为OutputStream，需要写出到的目标流
            writer.flush(out,true);
    // 关闭writer，释放内存
            out.close();
            writer.close();
            //
            }
    }

