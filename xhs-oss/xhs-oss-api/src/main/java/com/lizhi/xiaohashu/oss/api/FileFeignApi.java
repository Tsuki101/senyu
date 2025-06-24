package com.lizhi.xiaohashu.oss.api;

import com.lizhi.framework.common.response.Response;
import com.lizhi.xiaohashu.oss.config.FeignFormConfig;
import com.lizhi.xiaohashu.oss.constant.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
/*openfeign使用HTTP请求访问远程服务，就像调用本地方法一样的，开发者完全感知不到这是在调用远程方法*/
/**
 * @description: 对象存储服务接口
 **/
//若要调用/file/test接口，只需要引入xiaohashu-oss-api模块
@FeignClient(name = ApiConstants.SERVICE_NAME,configuration = FeignFormConfig.class)
public interface FileFeignApi {
    /*用于测试的接口*/
    String PREFIX = "/file";

    @PostMapping(value = PREFIX + "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Response<?> uploadFile(@RequestPart(value="file")MultipartFile file);

}

