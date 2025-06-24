package com.lizhi.xiaohashu.note.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.lizhi.xiaohashu.note.biz.domain.mapper")
@EnableFeignClients(basePackages = {"com.lizhi.xiaohashu"})
public class XiaohashuNoteBizApplication {
public static void main(String[] args){
    SpringApplication.run(XiaohashuNoteBizApplication.class, args);
}
}
