package entity;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;

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
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (requestAttributes != null) {


                // 获取所有 Http Headers 的 key
                Enumeration<String> headerNames = requestAttributes.getRequest().getHeaderNames();

                if (headerNames != null) {
                    while (headerNames.hasMoreElements()) {
                        // Http Headers 的 key
                        String name = headerNames.nextElement();

                        // Http Headers 的 value
                        /*String values = request.getHeader(name);*/
                        String values = requestAttributes.getRequest().getHeader(name);

                        // 将令牌数据添加到 Http Headers 中
                        requestTemplate.header(name, values);

                        System.out.println(name + ":" + values);
                    }
                }
            }
        } catch (Exception e) {

        }
    }
}
