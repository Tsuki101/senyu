package com.lizhi.xiaohashu.gateway.filter;

import cn.hutool.core.collection.CollUtil;
import com.lizhi.framework.common.constant.GlobalConstants;
import com.lizhi.xiaohashu.gateway.constant.RedisKeyConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

/**
 * @description: 转发请求时，将用户 ID 添加到 Header 请求头中，透传给下游服务(例如退出登陆服务需要知道当前用户ID)
 **/
@Component
@Slf4j
public class AddUserId2HeaderFilter implements GlobalFilter {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    private static final String TOKEN_HEADER_KEY = "Authorization";
    private static final String TOKEN_HEADER_VALUE_PREFIX = "Bearer ";
    /**
     * 请求头中，用户 ID 的键
     */
    private static final String HEADER_USER_ID = "userId";
//Satokenconfigure中SaReactorFilter优先级更高
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("==================> TokenConvertFilter");
        List<String> tokenList = exchange.getRequest().getHeaders().get(TOKEN_HEADER_KEY);
        if(CollUtil.isEmpty(tokenList)){
            return chain.filter(exchange);
        }
        String tokenValue= tokenList.get(0);
        String token = tokenValue.replace(TOKEN_HEADER_VALUE_PREFIX,"");
        String tokenRedisKey= RedisKeyConstants.SA_TOKEN_TOKEN_KEY_PREFIX+token;
        Integer userId=(Integer) redisTemplate.opsForValue().get(tokenRedisKey);
        //用户ID
        //Long userId =null;
//        try{
//            userId= StpUtil.getLoginIdAsLong();
//        }//获取当前登陆用户的ID
//        catch (Exception e){return chain.filter(exchange);}//若没登陆则直接放行
        if(Objects.isNull(userId)){
            return chain.filter(exchange);
        }
        log.info("## 当前登录的用户 ID: {}", userId);
        //Long finalUserId = userId;
//        ServerWebExchange newExchange =exchange.mutate().request(
//                builder->builder.header(HEADER_USER_ID,String.valueOf(finalUserId))
//        ).build();
        ServerWebExchange newExchange =exchange.mutate().request(
                builder->builder.header(GlobalConstants.USER_ID,String.valueOf(userId))
        ).build();
        return chain.filter(newExchange);
    }
}


