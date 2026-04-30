package cn.jxufe.valuexu.softwarestoreserver.utils;

import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    /**
     * 过期时间---24 hour
     */
    private static final int EXPIRATION_TIME = 60 * 60 * 24;
    /**
     * 生成随机密钥
     */
    private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
    /**
     * 前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";
    /**
     * 表头授权
     */
    public static final String AUTHORIZATION = "Authorization";

    private JwtUtil() {
    }

    /**
     * 功能描述:创建Token
     *
     * @param:
     * @return:
     */
    public static String generateToken(String username, int type) {
        Calendar calendar = Calendar.getInstance();
        // 设置签发时间
        Date now = calendar.getTime();

        // 设置过期时间
        calendar.setTime(new Date());
        // 添加秒钟
        calendar.add(Calendar.SECOND, EXPIRATION_TIME);
        Date expireTime = calendar.getTime();

        // 可以把任何数据放进map中，不只用户名
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("type", type);

        String jwt = Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .claims(map)
                // 签发时间
                .issuedAt(now)
                // 过期时间
                .expiration(expireTime)
                .signWith(SECRET_KEY)
                .compact();
        // jwt前面一般都会加Bearer
        return TOKEN_PREFIX + jwt;
    }

    /**
     * 功能描述: 解密Token
     */
    public static Map<String, Object> validateToken(String token) throws JwtException, IllegalArgumentException {
        String jws = token.replace(TOKEN_PREFIX, "");
        // parse the token.
        Claims payload = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(jws)
                .getPayload();
        String username = payload.get("username").toString();
        int type = Integer.parseInt(payload.get("type").toString());
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("type", type);
        return map;
    }
}
