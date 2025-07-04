package com.lizhi.xiaohashu.user.biz.domain.mapper;

import com.lizhi.xiaohashu.user.biz.domain.dataobject.RoleDO;

import java.util.List;

public interface RoleDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RoleDO record);

    int insertSelective(RoleDO record);

    RoleDO selectByPrimaryKey(Long id);
    List<RoleDO> selectEnabledList();
    int updateByPrimaryKeySelective(RoleDO record);

    int updateByPrimaryKey(RoleDO record);
}