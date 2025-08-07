package com.wg.appframe.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class WitherUrlUtils {

    /**
     *  如果配置了忽略的url, 则判断是否忽略
     * @param whiteStr
     * @return
     */
    public static boolean isPassFlag(String whiteStr) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        boolean passFlag = false;
        if (null == attributes) {
            return passFlag;
        }
        HttpServletRequest request = attributes.getRequest();
        if (null == request) {
            return passFlag;
        }
        String url = request.getRequestURI();
        if (StringUtils.isNotBlank(whiteStr)) {
            String[] whiteList = whiteStr.split(",");
            String httpUrl = "";
            for (String ignore : whiteList) {
                httpUrl = ignore;
                if (ignore.contains("/**")) {
                    httpUrl = httpUrl.replace("/**", "");
                    if (url.startsWith(httpUrl)) {
                        passFlag = true;
                        break;
                    }
                } else if (url.equals(httpUrl)) {
                    passFlag = true;
                    break;
                }
            }
        }
        return passFlag;
    }
}
