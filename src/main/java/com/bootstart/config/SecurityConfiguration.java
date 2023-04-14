package com.bootstart.config;

import com.bootstart.service.UserAuthService;
import com.fasterxml.jackson.databind.util.JSONPObject;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends GlobalAuthenticationConfigurerAdapter{

    @Resource
    DataSource dataSource;



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize)->authorize
                        .requestMatchers("/login","/static/**","/doLogin").permitAll()//无需授权即可访问的url，多个地址可以这样写。
                        .requestMatchers("/company").permitAll()//也可以写一个地址。
                        .anyRequest().authenticated())//其它页面需要授权才可以访问。
                .formLogin(form->form
                        .loginPage("/login")//自定义的表单，可以不用框架给的默认表单。
                        .loginProcessingUrl("/doLogin")//这个地址就是摆设，给外人看的，只要跟form表单的action一致就好，真正起作用的还是UserDetailsService。
                        .permitAll()
                        //.successForwardUrl("/"))
                        .defaultSuccessUrl("/index"))//建议用这个，successForwardUrl只能返回指定页，而这个可以返回来源页，没有来源页才会返回指定页，体验较好。
                .logout()//注销登录
                .logoutUrl("/logout")//这个地址也只是给人看的，只要跟前端注销地址一致就可。
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout","GET"))//主要是指定注销时用什么请求方法，GET和POST都可以吧。
                .logoutSuccessUrl("/login-form")//注销后要跳转的页面，那个页面一定是不需要授权就可以访问的，否则会出现意外结果。一般是首页，这里随便写的一个页面。
                .permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/unAuth.html")  //错误页面
                .and()
                .csrf()
                .and()
                .rememberMe()
                .tokenRepository(new JdbcTokenRepositoryImpl(){{setDataSource(dataSource);}});//自定义没权限访问的提示页面。
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
