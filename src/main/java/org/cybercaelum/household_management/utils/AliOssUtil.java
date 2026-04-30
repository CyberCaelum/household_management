package org.cybercaelum.household_management.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 阿里云OSS工具类
 * @date 2025/10/23 下午7:06
 */
@Data
@AllArgsConstructor
@Slf4j
public class AliOssUtil {
    private OSS ossClient;
    private String bucketName;

    /**
     * @description 文件上传
     * @author CyberCaelum
     * @date 下午8:58 2025/10/23
     * @param bytes 文件
     * @param objectName 文件名
     * @return java.lang.String 上传成功的objectName，失败返回null
     **/
    public String upload(byte[] bytes, String objectName) {
        try {
            // 创建PutObject请求。
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
        } catch (OSSException oe) {
            log.error("OSS上传异常, Error Message:{}, Error Code:{}, Request ID:{}, Host ID:{}",
                    oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
            return null;
        } catch (Exception ce) {
            log.error("OSS客户端异常, Error Message:{}", ce.getMessage());
            return null;
        }

        return objectName;
    }

    /**
     * @description 获取OSS对象
     * @author CyberCaelum
     * @date 下午9:15 2025/10/23
     * @param objectName 文件名
     * @return com.aliyun.oss.model.OSSObject
     **/
    public OSSObject getObject(String objectName) {
        return ossClient.getObject(bucketName, objectName);
    }

}
