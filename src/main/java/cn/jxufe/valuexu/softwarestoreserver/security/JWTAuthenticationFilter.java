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


        //??????????????????????????????
        try {
            if (null != SpringSecurityConfig.AUTH_WHITELIST && Arrays.asList(SpringSecurityConfig.AUTH_WHITELIST).contains(url)) {
                chain.doFilter(request, response);
                return;
            }
        } catch (Exception e) {
            logger.error("???????????????????????????" + e.getMessage());
        }

        //???????????????Token??????????????????
        if (StringUtils.isBlank(header) || !header.startsWith(JwtUtil.TOKEN_PREFIX)) {
            content.setCode(-1);
            content.setMsg("Token??????");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(content));
            return;
        }

        try {
            // ?????????????????????
            UsernamePasswordAuthenticationToken authentication = getAuthentication(request, response);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            content.setCode(-2);
            content.setMsg("Token??????");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(content));
            logger.error("Token?????????: {} ", e);
        } catch (UnsupportedJwtException e) {
            content.setCode(-3);
            content.setMsg("Token????????????");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(content));
            logger.error("Token????????????: {} ", e);
        } catch (MalformedJwtException e) {
            content.setCode(-4);
            content.setMsg("Token????????????");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(content));
            logger.error("Token?????????????????????: {} ", e);
        } catch (SignatureException e) {
            content.setCode(-5);
            content.setMsg("Token????????????");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(content));
            logger.error("????????????: {} ", e);
        } catch (IllegalArgumentException e) {
            content.setCode(-6);
            content.setMsg("Token??????????????????");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(content));
            logger.error("??????????????????: {} ", e);
        } catch (Exception e) {
            content.setCode(-9);
            content.setMsg("Invalid Token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(content));
            logger.error("Invalid Token ", e);
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
                // ??????Token
                HashMap<String, Object> jwtResult = JwtUtil.validateToken(token);
                username = jwtResult.get("username").toString();
                type = Integer.parseInt(jwtResult.get("type").toString());
                Optional<User> userFromSQL = userRepository.findByUsername(username);
//                if (!userFromSQL.isPresent()) {
//                    throw new Exception("Token?????????????????????");
//                }
//                if (userFromSQL.get().getType() != type) {
//                    throw new Exception("Token??????????????????");
//                }
                if (StringUtils.isNotBlank(username)) {
                    return new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                }
            } catch (ExpiredJwtException e) {
                throw e;
                //throw new TokenException("Token?????????");
            } catch (UnsupportedJwtException e) {
                throw e;
                //throw new TokenException("Token????????????");
            } catch (MalformedJwtException e) {
                throw e;
                //throw new TokenException("Token?????????????????????");
            } catch (SignatureException e) {
                throw e;
                //throw new TokenException("????????????");
            } catch (IllegalArgumentException e) {
                throw e;
                //throw new TokenException("??????????????????");
            } catch (Exception e) {
                throw e;
                //throw new IllegalStateException("Invalid Token. "+e.getMessage());
            }
            return null;
        }
        return null;
    }

}
