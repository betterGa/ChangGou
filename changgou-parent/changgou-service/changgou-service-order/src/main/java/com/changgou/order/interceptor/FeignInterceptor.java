package com.changgou.order.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Component
public class FeignInterceptor implements RequestInterceptor {
    /**
     * Feign 执行之前进行拦截
     *
     * @param requestTemplate
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {

        /***
         *  获取用户令牌
         */
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            // 取出 Request
            HttpServletRequest request = requestAttributes.getRequest();

            // 获取所有 Http Headers 的 key
            Enumeration<String> headerNames = request.getHeaderNames();

            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    // Http Headers 的 key
                    String name = headerNames.nextElement();

                    // Http Headers 的 value
                    String values = request.getHeader(name);


                    // 将令牌数据添加到 Http Headers 中
                    requestTemplate.header(name, values);

                    System.out.println(name+":"+values );
                }
            }
        }
    }
}
