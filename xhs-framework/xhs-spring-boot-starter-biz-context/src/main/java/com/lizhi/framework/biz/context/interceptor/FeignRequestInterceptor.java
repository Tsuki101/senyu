package com.lizhi.framework.biz.context.interceptor;

import com.lizhi.framework.biz.context.holder.LoginUserContextHolder;
import com.lizhi.framework.common.constant.GlobalConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class FeignRequestInterceptor implements RequestInterceptor {
@Override
    public void apply(RequestTemplate requestTemplate){
    Long userId = LoginUserContextHolder.getUserId();
    if(Objects.nonNull(userId)){
        requestTemplate.header(GlobalConstants.USER_ID,String.valueOf(userId));
        log.info("#####feign requestTemplate add header userId:{}",userId);
    }
}
}

