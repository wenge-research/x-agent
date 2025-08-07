package com.wg.appframe.core.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.SecureUtil;
import com.wg.appframe.core.constant.enums.ResultCodeEnum;
import com.wg.appframe.core.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 接口参数验签拦截器
 */
@Service
@Slf4j
@ConditionalOnProperty(prefix = "appframe.cipher", name = "enable", havingValue = "true")
public class CipherPreHandle implements PreHandle {

    /**
     * 接口加密秘钥
     */
    @Value("${appframe.cipher.md5Key}")
    private String md5Key;

    /**
     * 是否开启接口加密
     */
    @Value("${appframe.cipher.enable:true}")
    private boolean enable;

    /**
     * 白名单，不进行验签
     */
    @Value("${appframe.cipher.whiterApi:}")
    private List<String> whiterApiList;

    /**
     * 设置请求超时时间，单位秒
     */
    @Value("${cipher.timeout:20000}")
    private long cipherTimeout;

    private static long CIPHER_TIMEOUT;
    private static String MD5_KEY;

    @PostConstruct
    public void init() {
        CIPHER_TIMEOUT = cipherTimeout;
        MD5_KEY = md5Key;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!enable) {
            return true;
        }

        String url = request.getRequestURI();
        if (CollectionUtil.isNotEmpty(whiterApiList)) {
            for (String uri : whiterApiList) {
                // 后缀：/demo/*
                if (uri.endsWith("*")) {
                    String[] split = uri.split("\\*");
                    if (split.length == 0) {
                        return true;
                    }
                    if (StringUtils.isBlank(split[0])) {
                        return true;
                    }
                    if (url.startsWith(split[0])) {
                        return true;
                    }
                } else if (uri.endsWith(url)) {
                    // 全匹配
                    return true;
                }
            }
        }
        // 验证请求体数据
        return checkCipher(request, response);
    }

    /**
     * 验证密文
     *
     * @param request
     * @param response
     * @return
     */
    public static boolean checkCipher(HttpServletRequest request, HttpServletResponse response) {
        try {
            String cipher = request.getHeader("cipher");
            String timestamp = request.getHeader("timestamp");
            if (StringUtils.isBlank(timestamp)) {
                response.setStatus(417);
                log.error("====AuthInterceptor.checkCipher.md5.check.timestamp.is.blank====");
                ResponseUtil.resultMsg(response, ResultCodeEnum.TIMESTAMP_IS_BLANK);
                return false;
            }

            // 请求的时间差不允许超过cipherTimeout秒
            long allowTime = System.currentTimeMillis() - CIPHER_TIMEOUT;
            if (Long.parseLong(timestamp) < allowTime) {
                response.setStatus(417);
                log.error("====AuthInterceptor.checkCipher.md5.check.timestamp.is.timeout====");
                ResponseUtil.resultMsg(response, ResultCodeEnum.TIMESTAMP_IS_TIMEOUT);
                return false;
            }

            if (StringUtils.isBlank(cipher)) {
                response.setStatus(417);
                log.error("====AuthInterceptor.checkCipher.md5.check.cipher.is.blank====");
                ResponseUtil.resultMsg(response, ResultCodeEnum.CIPHER_IS_BLANK);
                return false;
            }
            StringBuilder plaintext = new StringBuilder(timestamp);

            // 对请求url体加密
            String url = request.getRequestURI();
            plaintext.append(url);

            // 对请求体加密
            String body = request.getReader().lines().collect(Collectors.joining());
            if (StringUtils.isNotBlank(body)) {
                plaintext.append(body);
            }

            // 对请求url中的参数加密
            String queryString = request.getQueryString();
            if (StringUtils.isNotBlank(queryString)) {
                // 转码
                queryString = URLUtil.decode(queryString);
                plaintext.append(queryString);
            }

            return check(plaintext.toString(), cipher, response);
        } catch (Exception e) {
            log.error("AuthInterceptor.checkCipher.error:{}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 验证加密
     *
     * @param plaintext 请求参数
     * @param cipher    传入密文
     * @param response
     * @return
     */
    private static boolean check(String plaintext, String cipher, HttpServletResponse response) {
        System.out.println("==========+" + plaintext);
        // 使用MD5加密
        String encryption = SecureUtil.md5(plaintext + MD5_KEY);
        log.info("--->plaintext:{},encryption:{},cipher:{}", plaintext + MD5_KEY, encryption, cipher);
        // 验证密文与加密是否一致，若一致则通过，若不一致则打回
        if (!cipher.equals(encryption)) {
            response.setStatus(417);
            log.info("------->  AuthInterceptor.checkCipher.md5.check.not pass");
            log.info("===> plaintext:{},cipher:{}", plaintext, cipher);
            ResponseUtil.resultMsg(response, ResultCodeEnum.CIPHER_IS_FAILED);
            return false;
        }
        return true;
    }

    @Override
    public int getSort() {
        return 1;
    }
}
