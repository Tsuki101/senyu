package com.lizhi.xhs.auth;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication

@EnableFeignClients(basePackages = "com.lizhi.xiaohashu")
public class XhsAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(XhsAuthApplication.class, args);

    }

}
