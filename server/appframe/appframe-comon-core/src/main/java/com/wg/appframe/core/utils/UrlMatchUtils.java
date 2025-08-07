package com.wg.appframe.core.utils;

import com.wg.appframe.core.constant.StringConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class UrlMatchUtils {


    /**
     * 判断是否通过白名单验证
     *
     * @param whiteStr
     * @param url
     * @return
     */
    public static boolean isPassFlag(String whiteStr, String url) {
        boolean passFlag = false;
        // String url = request.getRequestURI();
        if (StringUtils.isNotBlank(whiteStr)) {
            String[] whiteList = whiteStr.split(",");
            for (String ignore : whiteList) {
                String httpUrl = ignore;
                if (ignore.endsWith("/**")) {
                    httpUrl = httpUrl.replace("/**", StringConstant.BLANK);
                    if (url.startsWith(httpUrl)) {
                        passFlag = true;
                        break;
                    }
                }
                if (url.equals(httpUrl)) {
                    passFlag = true;
                    break;
                }
            }
        }
        log.info("==>>是否匹配验证：{}，passFlag:{}", url, passFlag);
        return passFlag;
    }

}
