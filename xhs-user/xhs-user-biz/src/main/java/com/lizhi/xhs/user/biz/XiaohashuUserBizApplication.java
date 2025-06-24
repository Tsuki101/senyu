package com.lizhi.xiaohashu.user.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.lizhi.xiaohashu.user.biz.domain.mapper")
@EnableFeignClients(basePackages="com.lizhi.xiaohashu")//启用引入的 xiaohashu-oss-api 模块中定义好的 Feign 客户端
public class XiaohashuUserBizApplication {
    public static void main(String[] args) {

        SpringApplication.run(XiaohashuUserBizApplication.class, args);
    }

}
