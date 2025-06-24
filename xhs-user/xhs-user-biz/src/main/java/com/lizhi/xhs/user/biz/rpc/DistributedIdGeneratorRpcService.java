package com.lizhi.xiaohashu.user.biz.rpc;

import com.lizhi.xiaohashu.distributed.generator.api.DistributedIdGeneratorFeignApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**

 * @description: 分布式 ID 生成服务
 **/
@Component
public class DistributedIdGeneratorRpcService {

    @Resource
    private DistributedIdGeneratorFeignApi distributedIdGeneratorFeignApi;

    /**
     * Leaf 号段模式：小哈书 ID 业务标识 用户ID
     */
    private static final String BIZ_TAG_XIAOHASHU_ID = "leaf-segment-xiaohashu-id";
    private static final String BIZ_TAG_USER_ID = "leaf-segment-user-id";
    /**
     * 调用分布式 ID 生成服务生成小哈书 ID
     *
     * @return
     */
    public String getXiaohashuId() {
        return distributedIdGeneratorFeignApi.getSegmentId(BIZ_TAG_XIAOHASHU_ID);
    }
    public String getUserId(){
        return distributedIdGeneratorFeignApi.getSegmentId(BIZ_TAG_USER_ID);
    }
}

