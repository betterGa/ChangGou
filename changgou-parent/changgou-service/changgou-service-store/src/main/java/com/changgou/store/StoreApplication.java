package com.changgou.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan("com.changgou.store.dao")
@SpringBootApplication
@EnableEurekaClient
public class StoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(StoreApplication.class,args);
    }
}
