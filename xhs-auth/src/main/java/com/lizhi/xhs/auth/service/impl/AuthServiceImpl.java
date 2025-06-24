package com.lizhi.xhs.auth.service.impl;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.google.common.base.Preconditions;
import com.lizhi.framework.common.enums.DeletedEnum;
import com.lizhi.framework.common.enums.StatusEnum;
import com.lizhi.framework.common.exception.BizException;
import com.lizhi.framework.common.response.Response;
import com.lizhi.framework.common.util.JsonUtils;
import com.lizhi.xhs.auth.constant.RedisKeyConstants;
import com.lizhi.xhs.auth.constant.RoleConstants;

import com.lizhi.xhs.auth.enums.LoginTypeEnum;
import com.lizhi.xhs.auth.enums.ResponseCodeEnum;
//import com.lizhi.xhs.auth.filter.LoginUserContextHolder;
import com.lizhi.framework.biz.context.holder.LoginUserContextHolder;
import com.lizhi.xhs.auth.model.vo.user.UpdatePasswordReqVO;
import com.lizhi.xhs.auth.model.vo.user.UserLoginReqVO;
import com.lizhi.xhs.auth.rpc.UserRpcService;
import com.lizhi.xhs.auth.service.AuthService;
import com.lizhi.xiaohashu.user.dto.resp.FindUserByPhoneRspDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

import java.util.List;

/**

 * @description: TODO
 **/
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {


    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Resource(name ="taskExecutor")/*idea识别问题 不用管飘红*/
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private UserRpcService userRpcService;

    /**
     * 登录与注册
     *
     * @param userLoginReqVO
     * @return
     */
    @Override
    public Response<String> loginAndRegister(UserLoginReqVO userLoginReqVO) {
        String phone = userLoginReqVO.getPhone();
        Integer type = userLoginReqVO.getType();

        LoginTypeEnum loginTypeEnum = LoginTypeEnum.valueOf(type);
        //登陆类型错误
        if(Objects.isNull(loginTypeEnum)){
            throw new BizException(ResponseCodeEnum.LOGIN_TYPE_ERROR);
        }
        Long userId = null;

        // 判断登录类型
        switch (loginTypeEnum) {
            case VERIFICATION_CODE: // 验证码登录
                String verificationCode = userLoginReqVO.getCode();

                // 校验入参验证码是否为空
                Preconditions.checkArgument(StringUtils.isNotBlank(verificationCode),"验证码不能为空");
/*
                if (StringUtils.isBlank(verificationCode)) {
                    return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode(), "验证码不能为空");
                }
*/

                // 构建验证码 Redis Key
                String key = RedisKeyConstants.buildVerificationCodeKey(phone);
                // 查询存储在 Redis 中该用户的登录验证码
                String sentCode = (String) redisTemplate.opsForValue().get(key);

                // 判断用户提交的验证码，与 Redis 中的验证码是否一致
                if (!StringUtils.equals(verificationCode, sentCode)) {
                    throw new BizException(ResponseCodeEnum.VERIFICATION_CODE_ERROR);
                }
                //RPC 调用用户服务 注册用户
                Long userIdTmp=userRpcService.registerUser(phone);
                if(Objects.isNull(userIdTmp)){
                    throw new BizException(ResponseCodeEnum.LOGIN_FAIL);
                }
                userId=userIdTmp;
                /*// 通过手机号查询记录 没有将注册服务抽取之前
                UserDO userDO = userDOMapper.selectByPhone(phone);

                log.info("==> 用户是否注册, phone: {}, userDO: {}", phone, JsonUtils.toJsonString(userDO));

                // 判断是否注册
                if (Objects.isNull(userDO)) {
                    // 若此用户还没有注册，系统自动注册该用户
                    //
                    userId=registerUser(phone);//下面定义
                } else {
                    // 已注册，则获取其用户 ID
                    userId = userDO.getId();
                }*/
                break;
            case PASSWORD: // 密码登录
                // 2.20
                String password = userLoginReqVO.getPassword();
                //RPC 调用用户服务 查询用户信息
                FindUserByPhoneRspDTO findUserByPhoneRspDTO = userRpcService.findUserByPhone(phone);

                if (Objects.isNull(findUserByPhoneRspDTO)) {
                    throw new BizException(ResponseCodeEnum.USER_NOT_FOUND);

                }

                String encodePassword = findUserByPhoneRspDTO.getPassword();
                boolean isPasswordCorrect=passwordEncoder.matches(password, encodePassword);
                if(!isPasswordCorrect){
                    throw new BizException(ResponseCodeEnum.PHONE_OR_PASSWORD_ERROR);
                }
                userId = findUserByPhoneRspDTO.getId();
                break;
            default:
                break;
        }

        // SaToken 登录用户，并返回 token 令牌
        //
        // SaToken 登录用户, 入参为用户 ID
        StpUtil.login(userId);

        // 获取 Token 令牌
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        // 返回 Token 令牌
        return Response.success(tokenInfo.tokenValue);

    }

/*    public Long registerUser(String phone) {
        // 获取全局自增的小哈书 ID
        return transactionTemplate.execute(status -> { //更细粒度的事务控制
        try{
        Long xiaohashuId = redisTemplate.opsForValue().increment(RedisKeyConstants.XIAOHASHU_ID_GENERATOR_KEY);

        UserDO userDO = UserDO.builder()
                .phone(phone)
                .xiaohashuId(String.valueOf(xiaohashuId)) // 自动生成小红书号 ID
                .nickname("小红薯" + xiaohashuId) // 自动生成昵称, 如：小红薯10000
                .status(StatusEnum.ENABLE.getValue()) // 状态为启用
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(DeletedEnum.NO.getValue()) // 逻辑删除
                .build();

        // 添加入库
        userDOMapper.insert(userDO);

        //测试事务回滚 成功
        //int i=1/0;
        // 获取刚刚添加入库的用户 ID 全局自增
        Long userId = userDO.getId();

        // 给该用户分配一个默认角色
        UserRoleDO userRoleDO = UserRoleDO.builder()
                .userId(userId)
                .roleId(RoleConstants.COMMON_USER_ROLE_ID)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(DeletedEnum.NO.getValue())
                .build();
        userRoleDOMapper.insert(userRoleDO);
        RoleDO roleDO=roleDOMapper.selectByPrimaryKey(RoleConstants.COMMON_USER_ROLE_ID);
        // 将该用户的角色 ID 存入 Redis 中
        List<String> roles =new ArrayList<>(1);//初始容量;

        roles.add(roleDO.getRoleKey());
        String userRolesKey = RedisKeyConstants.buildUserRoleKey(userId);
        redisTemplate.opsForValue().set(userRolesKey, JsonUtils.toJsonString(roles));
        return userId;
    } catch (Exception e) {status.setRollbackOnly(); // 标记事务为回滚
            log.error("==> 系统注册用户异常: ", e);
            return null;}});}*/
    @Override
    public Response<?> updatePassword(UpdatePasswordReqVO updatePasswordReqVO){
        //get方法获取新密码
        String newPassword = updatePasswordReqVO.getNewPassword();
        //密码加密
        String encodePassword = passwordEncoder.encode(newPassword);

        userRpcService.updatePassword(encodePassword);

        return Response.success();
    }
    /*退出登录*/

    @Override
    public Response<?> logout() {
        // 退出登录 (指定用户 ID)
        Long userId= LoginUserContextHolder.getUserId();
        log.info("==>用户退出登陆, userId: {}", userId);
        threadPoolTaskExecutor.submit(() -> { /*线程池*/
            Long userId2 = LoginUserContextHolder.getUserId();
            log.info("==> 异步线程中获取 userId: {}", userId2);
        });
        StpUtil.logout(userId);
        return Response.success();
    }
/*@Transactional(rollbackFor = Exception.class)//直接加transactional注解会导致自调用事务回滚失败 没有经过代理对象（绕过了AOP）
    public Long registerUser(String phone) {
        // 获取全局自增的小哈书 ID

                Long xiaohashuId = redisTemplate.opsForValue().increment(RedisKeyConstants.XIAOHASHU_ID_GENERATOR_KEY);

                UserDO userDO = UserDO.builder()
                        .phone(phone)
                        .xiaohashuId(String.valueOf(xiaohashuId)) // 自动生成小红书号 ID
                        .nickname("小红薯" + xiaohashuId) // 自动生成昵称, 如：小红薯10000
                        .status(StatusEnum.ENABLE.getValue()) // 状态为启用
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .isDeleted(DeletedEnum.NO.getValue()) // 逻辑删除
                        .build();

                // 添加入库
                userDOMapper.insert(userDO);

                //测试事务回滚
                int i=1/0;
                // 获取刚刚添加入库的用户 ID
                Long userId = userDO.getId();

                // 给该用户分配一个默认角色
                UserRoleDO userRoleDO = UserRoleDO.builder()
                        .userId(userId)
                        .roleId(RoleConstants.COMMON_USER_ROLE_ID)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .isDeleted(DeletedEnum.NO.getValue())
                        .build();
                userRoleDOMapper.insert(userRoleDO);

                // 将该用户的角色 ID 存入 Redis 中
                List<Long> roles = Lists.newArrayList();
                roles.add(RoleConstants.COMMON_USER_ROLE_ID);
                String userRolesKey = RedisKeyConstants.buildUserRoleKey(phone);
                redisTemplate.opsForValue().set(userRolesKey, JsonUtils.toJsonString(roles));

                return userId;
            }*/
}

