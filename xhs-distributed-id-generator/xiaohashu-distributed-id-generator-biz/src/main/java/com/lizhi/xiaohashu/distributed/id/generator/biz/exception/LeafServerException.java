package com.lizhi.xiaohashu.distributed.id.generator.biz.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.INTERNAL_SERVER_ERROR)
public class LeafServerException extends RuntimeException {
    public LeafServerException(String msg) {
        super(msg);
    }
}
