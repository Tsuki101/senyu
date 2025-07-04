package com.lizhi.xiaohashu.user.biz.domain.mapper;

import com.lizhi.xiaohashu.user.biz.domain.dataobject.PermissionDO;

import java.util.List;

public interface PermissionDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PermissionDO record);

    int insertSelective(PermissionDO record);

    PermissionDO selectByPrimaryKey(Long id);
    List<PermissionDO> selectAppEnabledList();
    int updateByPrimaryKeySelective(PermissionDO record);

    int updateByPrimaryKey(PermissionDO record);
}