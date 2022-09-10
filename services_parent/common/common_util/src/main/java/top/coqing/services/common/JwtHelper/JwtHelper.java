package top.coqing.services.common.JwtHelper;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;
import top.coqing.services.model.tag.Tag;
import top.coqing.services.model.user.User;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

public class JwtHelper {
    //过期时间
    private static long tokenExpiration = 24*60*60*1000;
    //签名密钥
    private static String tokenSignKey = "xxxx";

    //根据 用户id，用户名 生产token
    public static String createToken(Long userId, String userName) {
        String token = Jwts.builder()
                .setSubject("ORDER-USER")
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .claim("userId", userId)
                .claim("userName", userName)
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    //根据token字符串得到 用户id
    public static Long getUserId(String token) {
        if(StringUtils.isEmpty(token)) return null;
        Jws<Claims> claimsJws
                    = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        Integer userId = (Integer)claims.get("userId");
        return userId.longValue();
    }

    //根据token字符串得到 用户名称
    public static String getUserName(String token) {
        if(StringUtils.isEmpty(token)) return "";
        Jws<Claims> claimsJws 
                    = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String)claims.get("userName");
    }



    public static String createToken(User user) {
        String json = JSON.toJSONString(user);
        String token = Jwts.builder()
                .setSubject("ORDER-USER")
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .claim("user", json)
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    public static User getUserFromToken(String token) {
        if(StringUtils.isEmpty(token)) return null;
        Jws<Claims> claimsJws
                = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        String json = (String)claims.get("user");
        User user = JSON.parseObject(json,User.class);
        return user;
    }

    public static void main(String[] args) {
//        String token = JwtHelper.createToken(1L, "hello");
//        System.out.println(token);
//        System.out.println(JwtHelper.getUserId(token));
//        System.out.println(JwtHelper.getUserName(token));

//        User user = new User();
//        user.setId(1l);
//        user.setRole(0);
//        ArrayList<Tag> tags = new ArrayList<>();
//        Tag tag = new Tag();
//        tag.setText("nb");
//        tags.add(tag);
//        user.setTagList(tags);
//        String token = createToken(user);
//        System.out.println(token.getBytes().length);
//        User userFromToken = getUserFromToken(token);
//        System.out.println(userFromToken);

        User user =null;

    }
}
