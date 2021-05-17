
package com.changgou.oauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@Order(-1)
class WebSecurityConfig extends WebSecurityConfigurerAdapter {


/***
     * 忽略安全拦截的URL
     * @param web
     * @throws Exception
     */

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/user/login",
                "/user/logout",
                "/css/**",
                "/oauth/login",
                "/login.html",
                "/login",
                "/data/**",
                "/fonts/**",
                "/img/**",
                "/js/**",
                "/weixin/pay/**"
        );
    }


    /***
     * 创建授权管理认证对象
     * @return
     * @throws Exception
     */


    // 把整个类都注释掉，报错，缺少 AuthenticationManager，然后把这个方法放开，就会报 401
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        AuthenticationManager manager = super.authenticationManagerBean();
        return manager;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
    }


/***
     * 采用BCryptPasswordEncoder对密码进行编码
     * @return
     */


    /*** 对 username、password 里面的 password 进行编码。所以，访问
     http://localhost:9001/user/login?username=sunwukong&password=123123
    时，数据库里的密码必须是编码过的，否则校验失败，错误码 401
    **/

    /*** 把这个方法注释掉，报错：
    java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null"
	at org.springframework.security.crypto.password.DelegatingPasswordEncoder$UnmappedIdPasswordEncoder.matches
    这是因为：
     从spring security 5.X开始，需要使用密码编码器，也就是需要对明文密码进行加密，而不允许 NoAppasswordEncoder（无密码编码器）
     本项目版本是 5.1.5
     **/

     @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

/****
     *
     * @param http
     * @throws Exception
     */

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .httpBasic()        //启用Http基本身份验证
                .and()
                .formLogin()       //启用表单身份验证
                .and()
                .authorizeRequests()    //限制基于Request请求访问
                .antMatchers("/user")
                .permitAll()
                .anyRequest()
                .authenticated();       //其他请求都需要经过验证


/**
         * 登录配置
         */

        http.formLogin()
                // 自定义登录地址
                .loginPage("/oauth/login")

                // 登录处理地址
                .loginProcessingUrl("/user/login");
    }
}
