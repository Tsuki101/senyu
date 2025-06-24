package com.lizhi.xiaohashu.user.biz.service;
import com.lizhi.framework.common.response.Response;
import com.lizhi.xiaohashu.user.biz.model.vo.UpdateUserInfoReqVO;
import com.lizhi.xiaohashu.user.dto.req.FindUserByIdReqDTO;
import com.lizhi.xiaohashu.user.dto.req.FindUserByPhoneReqDTO;
import com.lizhi.xiaohashu.user.dto.req.RegisterUserReqDTO;
import com.lizhi.xiaohashu.user.dto.req.UpdateUserPasswordReqDTO;
import com.lizhi.xiaohashu.user.dto.resp.FindUserByIdRspDTO;
import com.lizhi.xiaohashu.user.dto.resp.FindUserByPhoneRspDTO;

/**

 * @description: 用户业务
 **/
public interface UserService {
    /**
     * 更新用户信息
     *
     * @param updateUserInfoReqVO
     * @return
     */
    Response<?> updateUserInfo(UpdateUserInfoReqVO updateUserInfoReqVO);
    Response<Long> register(RegisterUserReqDTO registerUserReqDTO);
    Response<FindUserByPhoneRspDTO> findByPhone(FindUserByPhoneReqDTO findUserByPhoneReqDTO);
    Response<?> updatePassword(UpdateUserPasswordReqDTO updateUserPasswordReqDTO);
    Response<FindUserByIdRspDTO> findById(FindUserByIdReqDTO findUserByIdReqDTO);

}

