package top.sharehome.share_study.utils;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.hyperledger.fabric.client.CallOption;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.Network;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Signers;
import org.hyperledger.fabric.client.identity.X509Identity;
import org.springframework.stereotype.Component;
import top.sharehome.share_study.config.FabricGatewayConfig;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;


/**
 * FabricGateway 工具类
 *
 * @author cmy
 */
@Component
public class FabricGatewayUtil {
    @Resource
    private FabricGatewayConfig hyperLedgerFabricProperties;

    private Network network;

    public Network getNetwork() {
        return this.network;
    }

    private Contract contract;

    public Contract getContract() {
        return this.contract;
    }

    @PostConstruct
    private void FabricUtils() throws IOException, CertificateException, InvalidKeyException {

        BufferedReader certificateReader = Files.newBufferedReader(Paths.get(hyperLedgerFabricProperties.getCertificatePath()), StandardCharsets.UTF_8);

        X509Certificate certificate = Identities.readX509Certificate(certificateReader);

        BufferedReader privateKeyReader = Files.newBufferedReader(Paths.get(hyperLedgerFabricProperties.getPrivateKeyPath()), StandardCharsets.UTF_8);

        PrivateKey privateKey = Identities.readPrivateKey(privateKeyReader);

        Gateway gateway = Gateway.newInstance()
                .identity(new X509Identity(hyperLedgerFabricProperties.getMspId(), certificate))
                .signer(Signers.newPrivateKeySigner(privateKey))
                .connection(newGrpcConnection())
                .evaluateOptions(CallOption.deadlineAfter(5, TimeUnit.SECONDS))
                .endorseOptions(CallOption.deadlineAfter(15, TimeUnit.SECONDS))
                .submitOptions(CallOption.deadlineAfter(5, TimeUnit.SECONDS))
                .commitStatusOptions(CallOption.deadlineAfter(1, TimeUnit.MINUTES))
                .connect();
        network = gateway.getNetwork(hyperLedgerFabricProperties.getChannelName());
        //获取合约对象
        contract = network.getContract(hyperLedgerFabricProperties.getChainCodeName(), hyperLedgerFabricProperties.getContractName());

    }

    private ManagedChannel newGrpcConnection() throws IOException, CertificateException {
        Reader tlsCertReader = Files.newBufferedReader(Paths.get(hyperLedgerFabricProperties.getTlsCertPath()));
        X509Certificate tlsCert = Identities.readX509Certificate(tlsCertReader);

        return NettyChannelBuilder.forTarget(hyperLedgerFabricProperties.getNettyChannelBuilder())
                .sslContext(GrpcSslContexts.forClient().trustManager(tlsCert).build())
                .overrideAuthority(hyperLedgerFabricProperties.getOverrideAuthority())
                .build();
    }
}