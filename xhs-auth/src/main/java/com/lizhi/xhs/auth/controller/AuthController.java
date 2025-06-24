package com.lizhi.xhs.auth.controller;

import com.lizhi.framework.biz.operationlog.aspect.ApiOperationLog;
import com.lizhi.framework.common.response.Response;
import com.lizhi.xhs.auth.model.vo.user.UpdatePasswordReqVO;
import com.lizhi.xhs.auth.model.vo.user.UserLoginReqVO;
import com.lizhi.xhs.auth.service.AuthService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**

 * @description: TODO
 **/
@RestController
//@RequestMapping("/user")
@Slf4j
public class AuthController {

    @Resource
    private AuthService authService;

    @PostMapping("/login")
    @ApiOperationLog(description = "用户登录/注册")
    public Response<String> loginAndRegister(@Validated @RequestBody UserLoginReqVO userLoginReqVO) {
        return authService.loginAndRegister(userLoginReqVO);
    }
//无入参
    @PostMapping("/logout")
    @ApiOperationLog(description = "账号登出")
    public Response<?> logout() {
    return authService.logout();
    }
    @PostMapping("/password/update")
    @ApiOperationLog(description = "修改密码")
    public Response<?> updatePassword(@Validated@RequestBody UpdatePasswordReqVO updatePasswordReqVO) {
        return authService.updatePassword(updatePasswordReqVO);
    }
}
