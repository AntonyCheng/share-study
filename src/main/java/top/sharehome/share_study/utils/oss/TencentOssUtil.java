package top.sharehome.share_study.utils.oss;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * OSS配置工具类
 *
 * @author AntonyCheng
 */
@Component
public class TencentOssUtil implements InitializingBean {
    @Value("${tencent.cos.file.region}")
    private String region;
    @Value("${tencent.cos.file.secret-id}")
    private String secretId;
    @Value("${tencent.cos.file.secret-key}")
    private String secretKey;
    @Value("${tencent.cos.file.bucket-name}")
    private String bucketName;

    public static String END_POINT;
    public static String ACCESS_KEY_ID;
    public static String ACCESS_KEY_SECRET;
    public static String BUCKET_NAME;

    @Override
    public void afterPropertiesSet() throws Exception {
        END_POINT = region;
        ACCESS_KEY_ID = secretId;
        ACCESS_KEY_SECRET = secretKey;
        BUCKET_NAME = bucketName;
    }
}
