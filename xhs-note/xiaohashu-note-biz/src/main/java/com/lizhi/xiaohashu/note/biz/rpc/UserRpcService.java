package com.lizhi.xiaohashu.note.biz.rpc;

import com.lizhi.framework.common.response.Response;
import com.lizhi.xiaohashu.user.api.UserFeignApi;
import com.lizhi.xiaohashu.user.dto.req.FindUserByIdReqDTO;
import com.lizhi.xiaohashu.user.dto.resp.FindUserByIdRspDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserRpcService {
@Resource
    private UserFeignApi userFeignApi;
/*查询用户信息*/
    public FindUserByIdRspDTO findById(Long userId){
        FindUserByIdReqDTO findUserByIdReqDTO=new FindUserByIdReqDTO(userId);
        findUserByIdReqDTO.setId(userId);//新建一个查询请求对象
        Response<FindUserByIdRspDTO> response=userFeignApi.findById(findUserByIdReqDTO);
        if(Objects.isNull(response)||!response.isSuccess()){return null;}
        return response.getData();
    }
}
