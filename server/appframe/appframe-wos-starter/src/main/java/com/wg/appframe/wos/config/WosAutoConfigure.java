package com.wg.appframe.wos.config;

import com.wg.appframe.wos.utils.WosUtil;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;

@AutoConfiguration
@Slf4j
@ComponentScan("com.wg")
@ConditionalOnProperty(prefix = "appframe.minio", name = "enable", havingValue = "true")
public class WosAutoConfigure implements ApplicationContextInitializer<GenericApplicationContext> {
    @Value("${appframe.minio.endpoint}")
    private String endpoint;
    @Value("${appframe.minio.accessKey}")
    private String accessKey;
    @Value("${appframe.minio.secretKey}")
    private String secretKey;

    @Bean("minioClient")
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean
    public WosUtil wosUtil() {
        return new WosUtil();
    }

    @Override
    public void initialize(GenericApplicationContext applicationContext) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:META-INF/maven/com.wenge/appframe-wos-starter/pom.xml");

        try (InputStream inputStream = resource.getInputStream()) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model read = reader.read(inputStream);
            String version = read.getVersion();
            log.info("〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓  appframe-wos-starter (" + version + ")  〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓");
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
        }
    }
}
