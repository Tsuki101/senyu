package com.lizhi.xiaohashu.user.biz.controller;

import com.lizhi.framework.biz.operationlog.aspect.ApiOperationLog;
import com.lizhi.framework.common.response.Response;
import com.lizhi.xiaohashu.user.biz.model.vo.UpdateUserInfoReqVO;
import com.lizhi.xiaohashu.user.biz.service.UserService;
import com.lizhi.xiaohashu.user.dto.req.FindUserByIdReqDTO;
import com.lizhi.xiaohashu.user.dto.req.FindUserByPhoneReqDTO;
import com.lizhi.xiaohashu.user.dto.req.RegisterUserReqDTO;
import com.lizhi.xiaohashu.user.dto.req.UpdateUserPasswordReqDTO;
import com.lizhi.xiaohashu.user.dto.resp.FindUserByIdRspDTO;
import com.lizhi.xiaohashu.user.dto.resp.FindUserByPhoneRspDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 用户
 **/
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;
    /**
     * 用户信息修改
     *
     * @param updateUserInfoReqVO
     * @return
     */
    /*接收到的对象*/
    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<?> updateUserInfo(@Validated UpdateUserInfoReqVO updateUserInfoReqVO) {
        return userService.updateUserInfo(updateUserInfoReqVO);
    }
    //对其他服务提供的接口
    @PostMapping("/register")
    @ApiOperationLog(description = "用户注册")
    public Response<Long> register(@Validated @RequestBody RegisterUserReqDTO registerUserReqDTO) {
        return userService.register(registerUserReqDTO);
    }
    @PostMapping("/findByPhone")
    @ApiOperationLog(description = "手机号查询用户信息")
    public Response<FindUserByPhoneRspDTO> findByPhone(@Validated @RequestBody FindUserByPhoneReqDTO findUserByPhoneReqDTO) {
        return userService.findByPhone(findUserByPhoneReqDTO);
    }
    @PostMapping("/findById")
    @ApiOperationLog(description = "用户id查询用户信息")
    public Response<FindUserByIdRspDTO> findById(@Validated @RequestBody FindUserByIdReqDTO findUserByIdReqDTO) {
        return userService.findById(findUserByIdReqDTO);
    }
    @PostMapping("/password/update")
    @ApiOperationLog(description = "密码更新")
    public Response<?> updatePassword(@Validated @RequestBody UpdateUserPasswordReqDTO updateUserPasswordReqDTO) {
        return userService.updatePassword(updateUserPasswordReqDTO);
    }
}


