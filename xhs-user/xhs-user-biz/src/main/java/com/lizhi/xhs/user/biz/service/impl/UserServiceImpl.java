package com.lizhi.xiaohashu.user.biz.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Preconditions;
import com.lizhi.framework.biz.context.holder.LoginUserContextHolder;
import com.lizhi.framework.common.enums.DeletedEnum;
import com.lizhi.framework.common.enums.StatusEnum;
import com.lizhi.framework.common.exception.BizException;
import com.lizhi.framework.common.response.Response;
import com.lizhi.framework.common.util.JsonUtils;
import com.lizhi.framework.common.util.ParamUtils;
import com.lizhi.xiaohashu.oss.api.FileFeignApi;
import com.lizhi.xiaohashu.user.biz.constant.RedisKeyConstants;
import com.lizhi.xiaohashu.user.biz.constant.RoleConstants;
import com.lizhi.xiaohashu.user.biz.domain.dataobject.RoleDO;
import com.lizhi.xiaohashu.user.biz.domain.dataobject.UserDO;
import com.lizhi.xiaohashu.user.biz.domain.dataobject.UserRoleDO;
import com.lizhi.xiaohashu.user.biz.domain.mapper.RoleDOMapper;
import com.lizhi.xiaohashu.user.biz.domain.mapper.UserDOMapper;
import com.lizhi.xiaohashu.user.biz.domain.mapper.UserRoleDOMapper;
import com.lizhi.xiaohashu.user.biz.enums.ResponseCodeEnum;
import com.lizhi.xiaohashu.user.biz.enums.SexEnum;
import com.lizhi.xiaohashu.user.biz.model.vo.UpdateUserInfoReqVO;
import com.lizhi.xiaohashu.user.biz.rpc.DistributedIdGeneratorRpcService;
import com.lizhi.xiaohashu.user.biz.rpc.OssRpcService;
import com.lizhi.xiaohashu.user.biz.service.UserService;
import com.lizhi.xiaohashu.user.dto.req.FindUserByIdReqDTO;
import com.lizhi.xiaohashu.user.dto.req.FindUserByPhoneReqDTO;
import com.lizhi.xiaohashu.user.dto.req.RegisterUserReqDTO;
import com.lizhi.xiaohashu.user.dto.req.UpdateUserPasswordReqDTO;
import com.lizhi.xiaohashu.user.dto.resp.FindUserByIdRspDTO;
import com.lizhi.xiaohashu.user.dto.resp.FindUserByPhoneRspDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @description: 用户业务实现类
 **/
@Service
@Slf4j
public class UserServiceImpl implements UserService {
/*修改用户信息 调用对象存储微服务*/
    @Resource
    private UserDOMapper userDOMapper;
    @Resource
    private FileFeignApi fileFeignApi;
    @Resource
    private OssRpcService ossRpcService;

    @Resource
    private UserRoleDOMapper userRoleDOMapper;
    @Resource
    private RoleDOMapper roleDOMapper;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private DistributedIdGeneratorRpcService  distributedIdGeneratorRpcService;
    @Resource(name="taskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;


    //用户信息本地缓存
    private static final Cache<Long,FindUserByIdRspDTO> LOCAL_CACHE = Caffeine.newBuilder()
            .initialCapacity(10000)
            .maximumSize(10000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Long> register(RegisterUserReqDTO registerUserReqDTO){
        String phone = registerUserReqDTO.getPhone();
        //判断是否已经注册
        UserDO userDO1=userDOMapper.selectByPhone(phone);
        log.info("==> 用户是否注册, phone: {}, userDO: {}", phone, JsonUtils.toJsonString(userDO1));
        //已注册 返回ID
        if (Objects.nonNull(userDO1)) {
        return Response.success(userDO1.getId());
        }
        //注册

        //获取全局自增的小哈书ID(redis)
        //Long xiaohashuId=redisTemplate.opsForValue().increment(RedisKeyConstants.XIAOHASHU_ID_GENERATOR_KEY);

        //Rpc调用分布式ID生成服务
        String xiaohashuId = distributedIdGeneratorRpcService.getXiaohashuId();
        String userIdStr = distributedIdGeneratorRpcService.getUserId();
        Long userId = Long.valueOf(userIdStr);
        UserDO userDO = UserDO.builder().id(userId)
                .phone(phone)
                .xiaohashuId(xiaohashuId) // 自动生成小红书号 ID
                .nickname("小红薯" + xiaohashuId) // 自动生成昵称, 如：小红薯10000
                .status(StatusEnum.ENABLE.getValue()) // 状态为启用
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(DeletedEnum.NO.getValue()) // 逻辑删除
                .build();
        userDOMapper.insert(userDO);
        //Long userId = userDO.getId(); 获取添加入库的用户ID
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
        List<String> roles =new ArrayList<>(1);
        roles.add(roleDO.getRoleKey());
        String userRolesKey = RedisKeyConstants.buildUserRoleKey(userId);
        redisTemplate.opsForValue().set(userRolesKey, JsonUtils.toJsonString(roles));
        return Response.success(userId);
    }
    //根据手机号查询用户信息
    @Override
    public Response<FindUserByPhoneRspDTO> findByPhone(FindUserByPhoneReqDTO findUserByPhoneReqDTO) {
        String phone = findUserByPhoneReqDTO.getPhone();
        // 根据手机号查询用户信息
        UserDO userDO = userDOMapper.selectByPhone(phone);

        // 判空
        if (Objects.isNull(userDO)) {
            throw new BizException(ResponseCodeEnum.USER_NOT_FOUND);
        }

        // 构建返参
        FindUserByPhoneRspDTO findUserByPhoneRspDTO = FindUserByPhoneRspDTO.builder()
                .id(userDO.getId())
                .password(userDO.getPassword())
                .build();

        return Response.success(findUserByPhoneRspDTO);
    }
    //根据ID查询用户信息
    @Override
    public Response<FindUserByIdRspDTO> findById(FindUserByIdReqDTO findUserByIdReqDTO)
    {
        Long userId=findUserByIdReqDTO.getId();
        //从本地缓存中查询用户信息
        FindUserByIdRspDTO findUserByIdRspDTOLocalCache=LOCAL_CACHE.getIfPresent(userId);
        if(Objects.nonNull(findUserByIdRspDTOLocalCache)){
            log.info("==>命中本地缓存；{}",findUserByIdRspDTOLocalCache);
            return Response.success(findUserByIdRspDTOLocalCache);
        }
        //本地缓存未命中->redis中查询
        //用户缓存Redis Key 再从缓存中查询
        String userInfoRedisKey=RedisKeyConstants.buildUserInfoKey(userId);
        String userInfoRedisValue=(String)redisTemplate.opsForValue().get(userInfoRedisKey);

        if(StringUtils.isNotBlank(userInfoRedisValue)){
            FindUserByIdRspDTO findUserByIdRspDTO=JsonUtils.parseObject(userInfoRedisValue,FindUserByIdRspDTO.class);
            //将用户信息异步存入本地缓存
            threadPoolTaskExecutor.submit(()->{
                if(Objects.nonNull(findUserByIdRspDTO)){
                    LOCAL_CACHE.put(userId,findUserByIdRspDTO);
                }
            });
            return Response.success(findUserByIdRspDTO);

        }
        //Redis中未命中->数据库中查询
        //从数据库中查询用户信息
        UserDO userDO =userDOMapper.selectByPrimaryKey(userId);

        if(Objects.isNull(userDO)){
            threadPoolTaskExecutor.execute(()->{long expireSeconds = 60 + RandomUtil.randomInt(60);
                redisTemplate.opsForValue().set(userInfoRedisKey, "null", expireSeconds, TimeUnit.SECONDS);});
            throw new BizException(ResponseCodeEnum.USER_NOT_FOUND);
        }//給空数据一分钟的过渡时间 避免缓存穿透
        FindUserByIdRspDTO findUserByIdRspDTO=FindUserByIdRspDTO.builder()
                .id(userDO.getId())
                .nickName(userDO.getNickname())
                .avatar(userDO.getAvatar())
                .build();
        //将用户信息异步存入redis中，提升响应速度
        threadPoolTaskExecutor.submit(() -> {
            // 过期时间（保底1天 + 随机秒数，将缓存过期时间打散，防止同一时间大量缓存失效，导致数据库压力太大）
            long expireSeconds = 60*60*24 + RandomUtil.randomInt(60*60*24);
            redisTemplate.opsForValue()
                    .set(userInfoRedisKey, JsonUtils.toJsonString(findUserByIdRspDTO), expireSeconds, TimeUnit.SECONDS);
        });
        return Response.success(findUserByIdRspDTO);
    }
    /**
     * 更新用户信息
     * @param updateUserInfoReqVO
     * @return
     */
    @Override
    public Response<?> updateUserInfo(UpdateUserInfoReqVO updateUserInfoReqVO) {
        UserDO userDO = new UserDO();
        // 设置当前需要更新的用户 ID
        userDO.setId(LoginUserContextHolder.getUserId());
        // 标识位：是否需要更新
        boolean needUpdate = false;

        // 头像
        MultipartFile avatarFile = updateUserInfoReqVO.getAvatar();

        if (Objects.nonNull(avatarFile)) {
            String avatar =ossRpcService.uploadFile(avatarFile);
            log.info("==> 调用oss服务成功，上传头像,url:{}",avatar);

            if(StringUtils.isBlank(avatar)){
                throw new BizException(ResponseCodeEnum.UPLOAD_AVATAR_FAIL);

            }
            userDO.setAvatar(avatar);
            needUpdate = true;

        }

        // 昵称
        String nickname = updateUserInfoReqVO.getNickname();
        if (StringUtils.isNotBlank(nickname)) {
            Preconditions.checkArgument(ParamUtils.checkNickname(nickname), ResponseCodeEnum.NICK_NAME_VALID_FAIL.getErrorMessage());
            userDO.setNickname(nickname);
            needUpdate = true;
        }

        // 小哈书号
        String xiaohashuId = updateUserInfoReqVO.getXiaohashuId();
        if (StringUtils.isNotBlank(xiaohashuId)) {
            Preconditions.checkArgument(ParamUtils.checkXiaohashuId(xiaohashuId), ResponseCodeEnum.XIAOHASHU_ID_VALID_FAIL.getErrorMessage());
            userDO.setXiaohashuId(xiaohashuId);
            needUpdate = true;
        }

        // 性别
        Integer sex = updateUserInfoReqVO.getSex();
        if (Objects.nonNull(sex)) {
            Preconditions.checkArgument(SexEnum.isValid(sex), ResponseCodeEnum.SEX_VALID_FAIL.getErrorMessage());
            userDO.setSex(sex);
            needUpdate = true;
        }

        // 生日
        LocalDate birthday = updateUserInfoReqVO.getBirthday();
        if (Objects.nonNull(birthday)) {
            userDO.setBirthday(birthday);
            needUpdate = true;
        }

        // 个人简介
        String introduction = updateUserInfoReqVO.getIntroduction();
        if (StringUtils.isNotBlank(introduction)) {
            Preconditions.checkArgument(ParamUtils.checkLength(introduction, 100), ResponseCodeEnum.INTRODUCTION_VALID_FAIL.getErrorMessage());
            userDO.setIntroduction(introduction);
            needUpdate = true;
        }

        // 背景图
        MultipartFile backgroundImgFile = updateUserInfoReqVO.getBackgroundImg();
        if (Objects.nonNull(backgroundImgFile)) {
            // todo: 调用对象存储服务上传文件
            String backgroundImg =ossRpcService.uploadFile(backgroundImgFile);
            log.info("==> 调用oss服务成功，上传背景图,url:{}",backgroundImg);
            if(StringUtils.isBlank(backgroundImg)){
                throw new BizException(ResponseCodeEnum.UPLOAD_BACKGROUND_IMG_FAIL);
            }
            userDO.setBackgroundImg(backgroundImg);
            needUpdate = true;
        }


        if (needUpdate) {
            // 更新用户信息
            userDO.setUpdateTime(LocalDateTime.now());
            userDOMapper.updateByPrimaryKeySelective(userDO);
        }
        return Response.success();
    }
//更新密码
    @Override
    public Response<?> updatePassword(UpdateUserPasswordReqDTO updateUserPasswordReqDTO) {
        // 获取当前请求对应的用户 ID
        Long userId = LoginUserContextHolder.getUserId();

        UserDO userDO = UserDO.builder()
                .id(userId)
                .password(updateUserPasswordReqDTO.getEncodePassword()) // 加密后的密码
                .updateTime(LocalDateTime.now())
                .build();
        // 更新密码
        userDOMapper.updateByPrimaryKeySelective(userDO);

        return Response.success();
    }
}

