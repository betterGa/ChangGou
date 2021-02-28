package com.changgou.goods;

import entity.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = {"com.changgou.goods.dao"})
@Configuration
@Import({entity.IdWorker.class})
public class GoodsApplication {
    public static void main(String[] args) {

        SpringApplication.run(GoodsApplication.class,args);
    }

    public IdWorker idWorker(){
        return new IdWorker(0,0);
    }
}
