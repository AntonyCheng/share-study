package top.sharehome.share_study.service.impl;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.StorageClass;
import com.qcloud.cos.region.Region;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeFileException;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;
import top.sharehome.share_study.service.FileOssService;
import top.sharehome.share_study.utils.TencentOssUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 文件ServiceImpl
 *
 * @author AntonyCheng
 */
@Service
public class FileOssServiceImpl implements FileOssService {
    @Override
    public String upload(MultipartFile file, String rootPath) {
        if (file == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "文件数据不能为空");
        }
        String name = file.getOriginalFilename();
        // 1 初始化用户身份信息（secretId, secretKey）
        //用户的 SecretId，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
        String secretId = TencentOssUtil.ACCESS_KEY_ID;
        //用户的 SecretKey，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
        String secretKey = TencentOssUtil.ACCESS_KEY_SECRET;
        String bucketName = TencentOssUtil.BUCKET_NAME;
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 2 设置 bucket 的地域
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region(TencentOssUtil.END_POINT);
        ClientConfig clientConfig = new ClientConfig(region);
        // 这里建议设置使用 https 协议
        // 从 5.6.54 版本开始，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 3 生成 cos 客户端并且上传文件
        COSClient cosClient = new COSClient(cred, clientConfig);
        // 指定要上传的文件
        // 对象键(Key)是对象在存储桶中的唯一标识
        String namePrefix = UUID.randomUUID().toString().replaceAll("-", "");
        String dataTime = new DateTime().toString("yyyy/MM/dd");
        String key = rootPath + "/" + dataTime + "/" + namePrefix + "_" + name;
        try {
            // 这里创建一个 ByteArrayInputStream 来作为示例，实际中这里应该是您要上传的 InputStream 类型的流
            InputStream inputStream = file.getInputStream();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(inputStream.available());
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, objectMetadata);
            // 设置存储类型（如有需要，不需要请忽略此行代码）, 默认是标准(Standard), 低频(standard_ia)
            putObjectRequest.setStorageClass(StorageClass.Standard_IA);
            cosClient.putObject(putObjectRequest);
            return "https://" + bucketName + "." + "cos" + "." + TencentOssUtil.END_POINT + ".myqcloud.com/" + key;
        } catch (CosClientException e) {
            throw new CustomizeFileException(R.failure(RCodeEnum.FILE_UPLOAD_EXCEPTION), "用户文件上传腾讯云OSS出现异常！");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String url) {
        if (StringUtils.isEmpty(url)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "文件地址不能为空");
        }
        // 1 初始化用户身份信息（secretId, secretKey）
        //用户的 SecretId，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
        String secretId = TencentOssUtil.ACCESS_KEY_ID;
        //用户的 SecretKey，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
        String secretKey = TencentOssUtil.ACCESS_KEY_SECRET;
        String bucketName = TencentOssUtil.BUCKET_NAME;
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 2 设置 bucket 的地域
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region(TencentOssUtil.END_POINT);
        ClientConfig clientConfig = new ClientConfig(region);
        // 这里建议设置使用 https 协议
        // 从 5.6.54 版本开始，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 3 生成 cos 客户端并且上传文件
        COSClient cosClient = new COSClient(cred, clientConfig);
        String[] split = url.split(".myqcloud.com/");
        if (split.length != 2) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.OSS_DELETES_OBJECTS_EXCEPTIONALLY), "链接错误");
        }
        String key = split[1];
        try {
            cosClient.deleteObject(bucketName, key);
        } catch (CosClientException e) {
            throw new CustomizeFileException(R.failure(RCodeEnum.OSS_DELETES_OBJECTS_EXCEPTIONALLY), "删除用户OSS已上传的无效文件失败");
        }
    }
}