package com.lizhi.framework.biz.context.config;

import com.lizhi.framework.biz.context.interceptor.FeignRequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class FeignContextAutoConfiguration {
@Bean
    public FeignRequestInterceptor feignRequestInterceptor(){
    return new FeignRequestInterceptor();
}
}
