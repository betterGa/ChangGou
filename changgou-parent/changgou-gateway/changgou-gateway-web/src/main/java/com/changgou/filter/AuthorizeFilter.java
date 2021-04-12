package com.changgou.filter;

import com.changgou.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局过滤器，实现用户权限鉴权
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {

    // 令牌的名字
    private static final String AUTHOR_TOKEN =  "Authorization";

    /**
     * 全局拦截
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //判断是否为登录的 URL 如果是,则放行
        if(URLFilter.hasAuthorize(request.getURI().toString())){
            return chain.filter(exchange);
        }


        // 获取用户令牌信息
        // 获取令牌信息的途径
        // （1）头文件中
        String token = request.getHeaders().getFirst(AUTHOR_TOKEN);

        // 设置一个布尔值，如果为 true，说明令牌在头文件中；
        boolean hasToken = true;

        if (StringUtils.isEmpty(token)) {
            // (2) 参数中
            token = request.getQueryParams().getFirst(AUTHOR_TOKEN);
            if (token != null) {
                hasToken = false;
            } else {
                // (3) Cookie 中
                HttpCookie cookie = request.getCookies().getFirst(AUTHOR_TOKEN);
                if (cookie != null) {
                    token = cookie.getValue();
                    hasToken = false;
                } else {

                    // 若没有令牌
                    // 设置状态码为 401 没有权限
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);

                    // 响应空数据
                    return response.setComplete();

                }

                // 如果有令牌，则校验令牌是否有效
                try {
                    // 如果解析成功
                    // JwtUtil.parseJWT(token);
                } catch (Exception e) {
                    e.printStackTrace();

                    // 无效，则拦截
                    // 设置状态码为 401 没有权限
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);

                    // 响应空数据
                    return response.setComplete();
                }
            }
        }

        //  需要将令牌封装到头文件，才能传给其他微服务
        if (!hasToken) {
            //request.mutate().header(AUTHOR_TOKEN,token);
            request.mutate().header(AUTHOR_TOKEN, "bearer " + token);
        }

        // 则放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
