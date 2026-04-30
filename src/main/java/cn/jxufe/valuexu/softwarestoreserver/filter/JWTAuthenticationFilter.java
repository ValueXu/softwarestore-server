package cn.jxufe.valuexu.softwarestoreserver.filter;

import cn.jxufe.valuexu.softwarestoreserver.config.SecurityConfig;
import cn.jxufe.valuexu.softwarestoreserver.dao.UserRepository;
import cn.jxufe.valuexu.softwarestoreserver.domain.User;
import cn.jxufe.valuexu.softwarestoreserver.dto.WrappedResDto;
import cn.jxufe.valuexu.softwarestoreserver.utils.JwtUtil;
import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class JWTAuthenticationFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public JWTAuthenticationFilter() {
        //
    }

    @Resource
    private UserRepository userRepository;

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request,
                                                                  HttpServletResponse response) throws Exception {
        String token = request.getHeader(JwtUtil.AUTHORIZATION);
        if (token == null) {
            return null;
        }
        String username = "";
        try {
            // 解密Token
            Map<String, Object> jwtResult = JwtUtil.validateToken(token);
            username = jwtResult.get("username").toString();
            int type = Integer.parseInt(jwtResult.get("type").toString());
            Optional<User> userFromSQL = userRepository.findByUsername(username);
            if (userFromSQL.isEmpty()) {
                throw new Exception("Token的用户名不存在");
            }
            if (userFromSQL.get().getType() != type) {
                throw new Exception("Token用户类型错误");
            }
            if (StringUtils.isNotBlank(username)) {
                return new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
            }
        } catch (ExpiredJwtException e) {
            throw e;
            // throw new TokenException("Token已过期");
        } catch (UnsupportedJwtException e) {
            throw e;
            // throw new TokenException("Token格式错误");
        } catch (MalformedJwtException e) {
            throw e;
            // throw new TokenException("Token没有被正确构造");
        } catch (SignatureException e) {
            throw e;
            // throw new TokenException("签名失败");
        } catch (IllegalArgumentException e) {
            throw e;
            // throw new TokenException("非法参数异常");
        } catch (Exception e) {
            throw e;
            // throw new IllegalStateException("Invalid Token. "+e.getMessage());
        }
        return null;
    }

    @Override
    public void doFilter(ServletRequest servletReq, ServletResponse servletRes, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletReq;
        HttpServletResponse response = (HttpServletResponse) servletRes;
        String url = request.getRequestURI();
        String header = request.getHeader(JwtUtil.AUTHORIZATION);

        // 跳过不需要验证的路径
        if (matchWhitListPatterns(url)) {
            chain.doFilter(servletReq, servletRes);
            return;
        }
        // 跳过options请求
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            chain.doFilter(servletReq, servletRes);
            return;
        }

        // 判断请求头Token字段是否为空
        if (StringUtils.isBlank(header) || !header.startsWith(JwtUtil.TOKEN_PREFIX)) {
            WrappedResDto<String> content = new WrappedResDto<>();
            content.setCode(-1);
            content.setMsg("Token为空");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            setResContentType(response);
            response.getWriter().write(JSON.toJSONString(content));
            return;
        }

        try {
            // 校验用户名密码
            UsernamePasswordAuthenticationToken authentication = getAuthentication(request, response);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (ExpiredJwtException e) {
            WrappedResDto<String> content = new WrappedResDto<>();
            content.setCode(-2);
            content.setMsg("Token过期");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(content));
            logger.error("Token已过期", e);
        } catch (UnsupportedJwtException e) {
            WrappedResDto<String> content = new WrappedResDto<>();
            content.setCode(-3);
            content.setMsg("Token格式错误");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(content));
            logger.error("Token格式错误", e);
        } catch (MalformedJwtException e) {
            WrappedResDto<String> content = new WrappedResDto<>();
            content.setCode(-4);
            content.setMsg("Token格式错误");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(content));
            logger.error("Token没有被正确构造", e);
        } catch (SignatureException e) {
            WrappedResDto<String> content = new WrappedResDto<>();
            content.setCode(-5);
            content.setMsg("Token签名错误");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(content));
            logger.error("签名失败", e);
        } catch (IllegalArgumentException e) {
            WrappedResDto<String> content = new WrappedResDto<>();
            content.setCode(-6);
            content.setMsg("Token非法参数异常");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(content));
            logger.error("非法参数异常", e);
        } catch (Exception e) {
            WrappedResDto<String> content = new WrappedResDto<>();
            content.setCode(-9);
            content.setMsg("Invalid Token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(content));
            logger.error("Invalid Token", e);
        }
        chain.doFilter(servletReq, servletRes);
    }

    private static void setResContentType(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
    }

    private boolean matchWhitListPatterns(String url) {
        if (null == SecurityConfig.AUTH_WHITELIST_PATTERNS) {
            return false;
        }
        return SecurityConfig.AUTH_WHITELIST_PATTERNS.stream().anyMatch(url::matches);
    }
}
