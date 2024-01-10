package com.example.garbageCollection.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.garbageCollection.service.IUserService;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenUtils {

//    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    /**
     * 密钥
     */
    private static final String SECRET = "my_secret";

    /**
     * 过期时间
     **/
    private static final long EXPIRATION = 1800L;//单位为秒


    private static  IUserService staticUserService; // 静态对象调用静态方法
    @Resource
    private IUserService userService;

    @PostMapping
    public void setUserService(){
        staticUserService = userService; // 赋值
    }

    /**
     * 生成用户token,设置token超时时间
     */
    public static String createToken(String userId, String sign) {
        //过期时间
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRATION * 1000);
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        String token = JWT.create()
                .withAudience(userId) // 用户id 作为载荷
                .withHeader(map)// 添加头部
                //可以将基本信息放到claims中
                .withExpiresAt(expireDate) //超时设置,设置过期的日期
                .withIssuedAt(new Date()) //签发时间
                .sign(Algorithm.HMAC256(sign)); //SECRET加密
        return token;
    }

    /*
    获取用户信息
    return user 对象
    ***/
//    public static User getUserInfo(){
//        try {
//            // 获取当前请求的request再把他解析出来，
//            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//            String token = request.getHeader("token");
//            if(StrUtil.isNotBlank(token)){
//                String userId = JWT.decode(token).getAudience().get(0);
//                return staticUserService.getById(Integer.valueOf(userId));
//            }
//        } catch (Exception e){
//            return null;
//        }
//        // 啥也不匹配，返回null;
//        return null;
//    }


}
