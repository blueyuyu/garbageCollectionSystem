package com.example.garbageCollection.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // 无参构造
@AllArgsConstructor // 有参构造
public class Result {
    private String code;
    private String msg;
    private Object data;

    public static Result success(){
        return new Result(Constants.CODE_200,"请求成功",null);
    }

    public static Result success(Object data){
        return new Result(Constants.CODE_200,"请求成功",data);
    }

    public static Result success(Object data,String message){
        return new Result(Constants.CODE_200,message,data);
    }

    // 普通错误
    public static Result error(String code,String msg){
        return new Result(code,msg,"");
    }

    // 系统错误
    public static Result error(){
        return new Result(Constants.CODE_500,"服务器错误","");
    }
}
