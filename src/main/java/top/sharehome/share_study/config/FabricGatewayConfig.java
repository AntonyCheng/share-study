package top.sharehome.share_study.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * FabricGateway 配置属性
 * @author cmy
 */
@Data
@Component
@ConfigurationProperties(prefix = "fabricgateway")
@Configuration
public class FabricGatewayConfig {
    /**
     * 用户证书路径
     */
    private String certificatePath;

    /**
     * 用户私钥路径
     */
    private String privateKeyPath;

    /**
     * tls证书lujing
     */
    private String tlsCertPath;

    /**
     * 通道名字
     */
    private String channelName;

    /**
     * mspId
     */
    private String mspId;

    /**
     * 链码名称
     */
    private String chainCodeName;

    /**
     * 合约名称
     */
    private String contractName;

    /**
     * 连接ip
     */
    private String nettyChannelBuilder;
    /**
     * 连接哪个peer节点
     */
    private String overrideAuthority;

}
