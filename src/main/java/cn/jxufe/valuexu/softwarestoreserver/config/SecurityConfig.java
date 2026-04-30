package cn.jxufe.valuexu.softwarestoreserver.config;

import cn.jxufe.valuexu.softwarestoreserver.filter.JWTAuthenticationFilter;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * 需要放行的URL
     */
    public static final List<String> AUTH_WHITELIST_PATTERNS = Collections.unmodifiableList(Arrays.asList(
            "/user/signIn*",
            "/software*",
            "/software/getAllByType*",
            "/user/signUp*",
            "/login*",
            "/error",
            "/actuator"));

    @Resource
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // 配置跨域和CSRF
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> {
                    // 配置API白名单（支持 Ant 风格通配符）
                    AUTH_WHITELIST_PATTERNS.forEach(path -> {
                        authorize
                                .requestMatchers(path)
                                .permitAll();
                    });
                    // 放行OPTIONS请求
                    authorize.requestMatchers(HttpMethod.OPTIONS).permitAll();
                    // 剩余API启用鉴权
                    authorize.anyRequest().authenticated();
                })
                // 禁用HTTP Basic
                .httpBasic(AbstractHttpConfigurer::disable)
                // 禁用OAuth2
                // .oauth2Client(oauth2 -> oauth2.disable())
                // 使用无状态Session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 添加jwt过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
