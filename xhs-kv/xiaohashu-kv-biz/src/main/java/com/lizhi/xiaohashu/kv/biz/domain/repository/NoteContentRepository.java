package com.lizhi.xiaohashu.kv.biz.domain.repository;


import com.lizhi.xiaohashu.kv.biz.domain.dataobject.NoteContentDO;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

/**

 * @description: TODO
 **/
//UUID为主键 NoteContentDO为与Cassandra数据库交互时使用的实体类，映射数据库中的表
public interface NoteContentRepository extends CassandraRepository<NoteContentDO, UUID> {

}

