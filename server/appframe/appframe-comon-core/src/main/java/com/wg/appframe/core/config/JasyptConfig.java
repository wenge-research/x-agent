package com.wg.appframe.core.config;


import cn.hutool.crypto.SecureUtil;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import com.wg.appframe.core.dto.params.JasyptParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("encryptablePropertyResolver")
@Slf4j
@ConditionalOnProperty(prefix = "appframe.jasypt", name = "enable", havingValue = "true")
public class JasyptConfig implements EncryptablePropertyResolver {

    @Value("${spring.application.name:application}")
    private String applicationName;

    @Value("${server.port:8080}")
    private String port;

    @Value("${appframe.jasypt.encryptor.algorithm:PBEWITHHMACSHA512ANDAES_256}")
    private String algorithm;

    @Value("${appframe.jasypt.enable:false}")
    private Boolean enable;

    @Value("${appframe.jasypt.encryptor.password:}")
    private String passwords;


    @Value("${appframe.jasypt.salt:123456}")
    private List<String> saltList;


    @Override
    public String resolvePropertyValue(String property) {
        try {
            if (null == enable || !enable) {
                return property;
            }
            if (StringUtils.isNotBlank(property) && property.startsWith("ENC|")) {
                log.info("===> jasypt.password:{}", property);
                JasyptParam param = new JasyptParam();
                param.setApplicationName(applicationName);
                param.setPort(port);
                param.setPassword(passwords);
                param.setValue(property);
                param.setAlgorithm(algorithm);
                param.setSaltList(saltList);
                String decrypt = decrypt(param);
                log.info("===> jasypt.decrypt:{}", decrypt);
                return decrypt;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return property;
    }

    private String decrypt(JasyptParam param) {
        if (StringUtils.isBlank(param.getAlgorithm())) {
            return "【算法】不能为空";
        }

        if (StringUtils.isBlank(param.getPassword())) {
            return "【秘钥】不能为空";
        }
        if (StringUtils.isBlank(param.getValue())) {
            return "【内容】不能为空";
        }

        return decryptStr(param);
    }

    /**
     * 标准解密数据
     *
     * @param param
     * @return
     */
    private String decryptStr(JasyptParam param) {
        // 1. 创建加解密工具实例
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();

        // 2. 加解密配置
        SimpleStringPBEConfig config = getConfig(param);
        encryptor.setConfig(config);

        // 3. 解密
        String encryptedText = param.getValue();
        encryptedText = encryptedText.replace("ENC|", "");

        return encryptor.decrypt(encryptedText);
    }

    /**
     * 封装配置
     *
     * @param param
     * @return
     */
    private SimpleStringPBEConfig getConfig(JasyptParam param) {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        String password = "";
        for (String salt : param.getSaltList()) {
            if (!StringUtils.isEmpty(password)) {
                password = SecureUtil.md5(password + SecureUtil.md5(salt));
            } else {
                password = SecureUtil.md5(SecureUtil.md5(param.getApplicationName() + SecureUtil.md5(param.getPassword())) + SecureUtil.md5(salt));
            }
        }
        config.setPassword(password);
        config.setAlgorithm(param.getAlgorithm());

        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        return config;
    }


    public String encrypt(String property) {
        JasyptParam param = new JasyptParam();
        param.setApplicationName(applicationName);
        param.setPort(port);
        param.setPassword(passwords);
        param.setValue(property);
        param.setAlgorithm(algorithm);
        if (StringUtils.isBlank(param.getAlgorithm())) {
            return "【算法】不能为空";
        }

        if (StringUtils.isBlank(param.getPassword())) {
            return "【秘钥】不能为空";
        }
        if (StringUtils.isBlank(param.getValue())) {
            return "【内容】不能为空";
        }

        return encryptStr(param);
    }

    /**
     * 标准加密数据
     *
     * @param param
     * @return
     */
    private String encryptStr(JasyptParam param) {
        // 1. 创建加解密工具实例
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();

        // 2. 加解密配置
        SimpleStringPBEConfig config = getConfig(param);

        encryptor.setConfig(config);

        // 3. 加密
        return encryptor.encrypt(param.getValue());
    }
}
