package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Locale;

@SpringBootApplication
@EnableEurekaClient
public class GatewayWebApplication {

    /**
     * 创建用户唯一标识，使用 IP 作为用户唯一标识，来根据 IP 进行限流操作
     * @return
     */
    @Bean(name = "ipKeyResolver")
    public KeyResolver userKeyResolver() {
        return new KeyResolver() {
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                String hostName = exchange.getRequest().getRemoteAddress().getAddress().getHostName();
                System.out.println("hostName" + hostName);
                return Mono.just(hostName);
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(GatewayWebApplication.class,args);
    }
}
