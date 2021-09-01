package cn.jxufe.valuexu.softwarestoreserver.security;

import cn.jxufe.valuexu.softwarestoreserver.response.ResponseBodyContent;
import com.alibaba.fastjson.JSON;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class Success implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {


        ResponseBodyContent<String> content = new ResponseBodyContent<>();
        //生成JWT并放置到响应体中
        String jwt=JwtUtil.generateToken("username",1);
        content.setResult(jwt);

        response.setContentType("application/json;charset:UTF-8");
        response.setCharacterEncoding("UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(JSON.toJSONString(content).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
