package com.example.garbageCollection.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.garbageCollection.common.Result;
import com.example.garbageCollection.entity.Files;
import com.example.garbageCollection.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

/**
 * 文件上传的一个接口
 */
@RestController
@RequestMapping("/file")
public class FileController {

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
}
