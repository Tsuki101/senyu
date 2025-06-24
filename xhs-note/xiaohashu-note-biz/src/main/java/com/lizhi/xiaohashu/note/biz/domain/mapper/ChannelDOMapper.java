package com.lizhi.xiaohashu.note.biz.domain.mapper;

import com.lizhi.xiaohashu.note.biz.domain.dataobject.ChannelDO;

public interface ChannelDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ChannelDO record);

    int insertSelective(ChannelDO record);

    ChannelDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ChannelDO record);

    int updateByPrimaryKey(ChannelDO record);
}