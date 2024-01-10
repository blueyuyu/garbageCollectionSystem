package com.example.garbageCollection.exception;

import lombok.Getter;

/**
 * 自定义异常 继承 runtime 异常
 *
 * */
@Getter
public class ServiceException extends RuntimeException{
    private String code; // 状态码
    public ServiceException(String code,String msg){
        super(msg);
        this.code = code;
    }
}
