package cn.jxufe.valuexu.softwarestoreserver.security;

import io.jsonwebtoken.*;

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
     * 自己设定的秘钥
     */
    private static final String SECRET = "023bdc63c3c5a4587*9ee6581508b9d03ad39a74fc0c9a9cce604743367c9646b";
    /**
     * 前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";
    /**
     * 表头授权
     */
    public static final String AUTHORIZATION = "Authorization";

    /**
     * 功能描述:创建Token
     *
     * @param:
     * @return:
     */
    public static String generateToken(String username,int type) {
        Calendar calendar = Calendar.getInstance();
        // 设置签发时间
        Date now = calendar.getTime();

        // 设置过期时间
        calendar.setTime(new Date());
        // 添加秒钟
        calendar.add(Calendar.SECOND, EXPIRATION_TIME);
        Date expireTime = calendar.getTime();

        //可以把任何数据放进map中，不只用户名
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("type",type);

        String jwt = Jwts.builder()
                .setHeaderParam("type", "JWT")
                //.setSubject(username)
                .setClaims(map)
                //签发时间
                .setIssuedAt(now)
                //过期时间
                .setExpiration(expireTime)
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
        //jwt前面一般都会加Bearer
        return TOKEN_PREFIX + jwt;
    }

    /**
     * 功能描述: 解密Token
     */
    public static HashMap<String,Object> validateToken(String token) {
        try {
            // parse the token.
            Map<String, Object> body = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody();
            String username = body.get("username").toString();
            int type=Integer.parseInt(body.get("type").toString());
            HashMap<String,Object> map=new HashMap<>();
            map.put("username",username);
            map.put("type",type);
            return map;
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (UnsupportedJwtException e) {
            throw e;
        } catch (MalformedJwtException e) {
            throw e;
        } catch (SignatureException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }
}
