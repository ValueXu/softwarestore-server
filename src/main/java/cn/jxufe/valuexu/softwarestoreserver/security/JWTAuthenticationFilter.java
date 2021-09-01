package cn.jxufe.valuexu.softwarestoreserver.security;

import cn.jxufe.valuexu.softwarestoreserver.dao.UserRepository;
import cn.jxufe.valuexu.softwarestoreserver.domain.User;
import cn.jxufe.valuexu.softwarestoreserver.response.ResponseBodyContent;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class JWTAuthenticationFilter extends BasicAuthenticationFilter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String url = request.getRequestURI();
        String header = request.getHeader(JwtUtil.AUTHORIZATION);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        JSONObject json = new JSONObject();
        ResponseBodyContent<String> content = new ResponseBodyContent<>();


        //跳过不需要验证的路径
        try {
            if (null != SpringSecurityConfig.AUTH_WHITELIST && Arrays.asList(SpringSecurityConfig.AUTH_WHITELIST).contains(url)) {
                chain.doFilter(request, response);
                return;
            }
        } catch (Exception e) {
            logger.error("白名单权限校验错误" + e.getMessage());
        }

        //判断请求头Token字段是否为空
        if (StringUtils.isBlank(header) || !header.startsWith(JwtUtil.TOKEN_PREFIX)) {
            content.setCode(-1);
            content.setMsg("Token为空");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(content));
            return;
        }

        try {
            // 校验用户名密码
            UsernamePasswordAuthenticationToken authentication = getAuthentication(request, response);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            content.setCode(-2);
            content.setMsg("Token过期");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(content));
            logger.error("Token已过期: {} " + e);
        } catch (UnsupportedJwtException e) {
            content.setCode(-3);
            content.setMsg("Token格式错误");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(content));
            logger.error("Token格式错误: {} " + e);
        } catch (MalformedJwtException e) {
            content.setCode(-4);
            content.setMsg("Token格式错误");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(content));
            logger.error("Token没有被正确构造: {} " + e);
        } catch (SignatureException e) {
            content.setCode(-5);
            content.setMsg("Token签名错误");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(content));
            logger.error("签名失败: {} " + e);
        } catch (IllegalArgumentException e) {
            content.setCode(-6);
            content.setMsg("Token非法参数异常");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(content));
            logger.error("非法参数异常: {} " + e);
        } catch (Exception e) {
            content.setCode(-9);
            content.setMsg("Invalid Token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(content));
            logger.error("Invalid Token " + e.getMessage());
        }
    }

    @Resource
    private UserRepository userRepository;

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader(JwtUtil.AUTHORIZATION);
        if (token != null) {
            String username = "";
            int type = 0;
            try {
                // 解密Token
                HashMap<String, Object> jwtResult = JwtUtil.validateToken(token);
                username = jwtResult.get("username").toString();
                type = Integer.parseInt(jwtResult.get("type").toString());
                Optional<User> userFromSQL = userRepository.findByUsername(username);
//                if (!userFromSQL.isPresent()) {
//                    throw new Exception("Token的用户名不存在");
//                }
//                if (userFromSQL.get().getType() != type) {
//                    throw new Exception("Token用户类型错误");
//                }
                if (StringUtils.isNotBlank(username)) {
                    return new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                }
            } catch (ExpiredJwtException e) {
                throw e;
                //throw new TokenException("Token已过期");
            } catch (UnsupportedJwtException e) {
                throw e;
                //throw new TokenException("Token格式错误");
            } catch (MalformedJwtException e) {
                throw e;
                //throw new TokenException("Token没有被正确构造");
            } catch (SignatureException e) {
                throw e;
                //throw new TokenException("签名失败");
            } catch (IllegalArgumentException e) {
                throw e;
                //throw new TokenException("非法参数异常");
            } catch (Exception e) {
                throw e;
                //throw new IllegalStateException("Invalid Token. "+e.getMessage());
            }
            return null;
        }
        return null;
    }

}
