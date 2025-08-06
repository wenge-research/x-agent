package com.wenge.oauth.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class RequestUtil {

    private static String BEARER_TYPE = "Bearer";

    public static String extractToken(HttpServletRequest request) {
        String token = extractHeaderAuthorizationToken(request);
        if (token == null) {
            token = request.getParameter("access_token");
            if (token == null) {
                token = extractHeaderAccessToken(request);
            }
        }

        if (StringUtils.isNotBlank(token)) {
            token = token.replaceAll(" ", "+");
        }

        return token;
    }

    /**
     * 解析header中的token(key为Authorization)
     *
     * @param request http请求
     * @return 返回token
     */
    private static String extractHeaderAuthorizationToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders("Authorization");
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if (value.startsWith(BEARER_TYPE)) {
                if (value.contains(BEARER_TYPE + "+")) {
                    value = value.replace(BEARER_TYPE + "+", BEARER_TYPE + " ");
                }
                String authHeaderValue = value.substring(BEARER_TYPE.length()).trim();
                int commaIndex = authHeaderValue.indexOf(',');
                if (commaIndex > 0) {
                    authHeaderValue = authHeaderValue.substring(0, commaIndex);
                }
                return authHeaderValue;
            }
        }
        return null;
    }


    /**
     * 解析header中的token(key为access_token)
     *
     * @param request http请求
     * @return 返回token
     */
    private static String extractHeaderAccessToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders("access_token");
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }
}
