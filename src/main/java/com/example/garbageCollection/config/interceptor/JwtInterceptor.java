package com.example.garbageCollection.config.interceptor;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.garbageCollection.common.Constants;
import com.example.garbageCollection.entity.User;
import com.example.garbageCollection.exception.ServiceException;
import com.example.garbageCollection.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt拦截器
 */
@Slf4j
//@Component 这里不能 加 @Component 不然在后面 webConfig new jwtInterceptor 实际，会报错
//could not be registered. A bean with that name has already been defined in file
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private IUserService userService;
//    这里为什么不是用的UserServiceImpl

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) {
        // 从 http 请求头中取出 token
        String token = httpServletRequest.getHeader("token");
        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        // 执行认证
        if (StrUtil.isBlank(token)) {
            throw new ServiceException(Constants.CODE_401,"无token，请重新登录");
        }
        // 获取 token 中的 userId
        String userId;
        try {
            userId = JWT.decode(token).getAudience().get(0);
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_401,"token认证失败");
        }
        User user = userService.getById(userId); // 验证有效
        if(user == null){
            throw new ServiceException(Constants.CODE_401,"用户不存在，请重新登录");
            // 不理解
        }
        // 用户密码加签验证
        JWTVerifier  jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try{
            jwtVerifier.verify(token);
        }catch (JWTVerificationException e){
            throw new ServiceException(Constants.CODE_401,"用户不存在，请重新登录");
        }
        // 验证 token
        return true;
    }
}
