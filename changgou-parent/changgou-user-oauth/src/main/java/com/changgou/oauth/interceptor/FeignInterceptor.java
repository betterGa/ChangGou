package com.changgou.oauth.interceptor;

import com.changgou.oauth.util.AdminToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Configuration
public class FeignInterceptor implements RequestInterceptor {
    /**
     * Feign 执行之前进行拦截
     *
     * @param requestTemplate
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {

        // 生成 Admin 令牌
        String token = AdminToken.adminToken();

        // 把令牌放在 Http Headers 中
        requestTemplate.header("Authorization", "bearer " + token);
    }
}
