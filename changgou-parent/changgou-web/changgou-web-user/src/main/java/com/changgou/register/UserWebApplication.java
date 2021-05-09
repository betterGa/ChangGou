package com.changgou.register;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class UserWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserWebApplication.class,args);
    }
}
