package cn.jxufe.valuexu.softwarestoreserver.security;

import cn.jxufe.valuexu.softwarestoreserver.response.ResponseBodyContent;
import com.alibaba.fastjson.JSON;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class Failure implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset:UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();

        ResponseBodyContent<String> content = new ResponseBodyContent<>();
        content.setCode(-1);
        content.setMsg(exception.getMessage());
        outputStream.write(JSON.toJSONString(content).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
