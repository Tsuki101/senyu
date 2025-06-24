package com.lizhi.xiaohashu.oss.biz.service.impl;
import com.lizhi.framework.common.response.Response;
import com.lizhi.xiaohashu.oss.biz.service.FileService;
import com.lizhi.xiaohashu.oss.biz.strategy.FileStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**

 * @description: TODO
 **/
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Resource
    private FileStrategy fileStrategy;

    private static final String BUCKET_NAME = "xiaohashu-lizhi";
    @Override
    public Response<?> uploadFile(MultipartFile file) {
        // 上传文件到
        String url=fileStrategy.uploadFile(file, BUCKET_NAME);

        return Response.success(url);
    }
}


