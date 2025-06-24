package com.lizhi.xiaohashu.distributed.id.generator.biz.core;


import com.lizhi.xiaohashu.distributed.id.generator.biz.core.common.Result;

public interface IDGen {
    Result get(String key);
    boolean init();
}
