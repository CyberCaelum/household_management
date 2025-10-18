package org.cybercaelum.household_management.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: jwt相关工具类
 * @date 2025/10/18 下午2:58
 */
public class JwtUtil {

    /**
     * @description 使用Hs256算法，生成jwt,密钥的长度至少是32位
     * @author CyberCaelum
     * @date 下午3:00 2025/10/18
     * @param secretKey jwt密钥
     * @param ttlMillis jwt过期时间
     * @param claims 设置的信息
     * @return java.lang.String 生成的jwt字符串
     **/
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        //生成jwt的时间,过期时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);
        //创建密钥对象
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        //设置jwt的内容
        JwtBuilder builder = Jwts.builder()
                //设置jwt要传递的内容
                .setClaims(claims)
                //设置签名使用的密钥
                .signWith(key)
                //设置过期时间
                .setExpiration(exp);
        return builder.compact();
    }

    /**
     * @description 密钥解密
     * @author CyberCaelum
     * @date 下午3:25 2025/10/18
     * @param secretKey jwt密钥
     * @param jwt 加密后的token
     * @return io.jsonwebtoken.Claims
     **/
    public static Claims parseJWT(String secretKey, String jwt) {
        //创建密钥对象
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                //设置签名的密钥
                .setSigningKey(key)
                .build()
                //设置需要解析的jwt
                .parseClaimsJws(jwt)
                .getBody();
    }
}
