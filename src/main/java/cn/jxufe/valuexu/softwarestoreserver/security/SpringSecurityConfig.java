package cn.jxufe.valuexu.softwarestoreserver.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
//@EnableWebSecurity 对于springboot来说不需要使用该注解
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * 需要放行的URL
     */
    public static final String[] AUTH_WHITELIST = {
            "/user/signIn",
            "/software",
            "/software/getAllByType",
            "/user/signUp",
            "/login"
    };

    //处理成功类
    @Resource
    private Success success;

    //处理异常类
    @Resource
    private Failure failure;

    @Bean
    JWTAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(authenticationManager());
        return jwtAuthenticationFilter;
    }



    @Resource
    private DataSource dataSource;

//    @Bean
//    public JdbcTokenRepositoryImpl tokenRepository() {
//        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
//        tokenRepository.setDataSource(dataSource);
//        //tokenRepository.setCreateTableOnStartup(true); // 启动创建表，创建成功后注释掉
//        return tokenRepository;
//    }
//
//    @Bean
//    UserDetailsService customUserService() { // 注册UserDetailsService 的bean
//        return new CustomUserServiceImpl();
//    }
//
//    @Override
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(customUserService()).passwordEncoder(new BCryptPasswordEncoder());
//    }

    /**
     * 配置请求拦截
     */
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                //由于使用的是JWT，我们这里不需要csrf
                .csrf().disable()
//                //登录配置
//                .formLogin()
//                .successHandler(success)
//                .failureHandler(failure)
//                .and()
                //基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                //可以匿名访问的链接
                .antMatchers(AUTH_WHITELIST).permitAll()
                //其他所有请求需要身份认证
                .anyRequest().authenticated()
                .and()
                .addFilter(jwtAuthenticationFilter());
    }
}
