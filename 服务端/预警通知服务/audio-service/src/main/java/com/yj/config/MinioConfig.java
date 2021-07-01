package com.yj.config;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    private String endpoint;

    private String accesskey;

    private String secretkey;
    //端口
    private int port;
    //bucket
    private String bucketName;

    @Bean
    public MinioClient minioClient() throws InvalidPortException, InvalidEndpointException {
        //minio默认MinioClient初始化secure为true使用https协议，
        //因此在初始化MinioClient时候修改secure为false代表使用http访问,
        //否则如果服务器不支持https则会报javax.net.ssl.SSLException: Unrecognized SSL message, plaintext connection?
        MinioClient client = new MinioClient(
                endpoint,
                port,
                accesskey,
                secretkey,
                true);
        return client;
    }

}
