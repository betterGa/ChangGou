package com.changgou;

import entity.FeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.changgou.goods.feign","com.changgou.order.feign"})
public class itemApplication {
    public static void main(String[] args) {
        SpringApplication.run(itemApplication.class,args);
    }
    @Bean
    public FeignInterceptor feignInterceptor() {
        return new FeignInterceptor();
    }
}
