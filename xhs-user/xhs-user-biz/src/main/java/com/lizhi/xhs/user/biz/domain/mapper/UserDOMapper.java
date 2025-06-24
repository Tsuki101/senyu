package com.lizhi.xiaohashu.user.biz.domain.mapper;

import com.lizhi.xiaohashu.user.biz.domain.dataobject.UserDO;

public interface UserDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserDO record);

    int insertSelective(UserDO record);

    UserDO selectByPrimaryKey(Long id);
    UserDO selectByPhone(String phone);
    int updateByPrimaryKeySelective(UserDO record);

    int updateByPrimaryKey(UserDO record);
}