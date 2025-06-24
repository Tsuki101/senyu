package com.lizhi.xhs.auth.service;


import com.lizhi.framework.common.response.Response;
import com.lizhi.xhs.auth.model.vo.user.UpdatePasswordReqVO;
import com.lizhi.xhs.auth.model.vo.user.UserLoginReqVO;
/**
 * @description: TODO
 **/
public interface AuthService {
    /**
     * 登录与注册
     * @param userLoginReqVO
     * @return
     */
    Response<String> loginAndRegister(UserLoginReqVO userLoginReqVO);
    Response<?> logout();
    Response<?> updatePassword(UpdatePasswordReqVO updatePasswordReqVO);
}

