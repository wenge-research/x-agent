package com.wg.appframe.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 生成APPID工具类
 * @author liuyutang
 */
@Slf4j
public class APPIDUtil {

    public static String generateAppID(String appName) {
        if (StringUtils.isNotBlank(appName)) {
            try {
                return genAppId(appName);
            } catch (NoSuchAlgorithmException e) {
                log.error(e.getMessage());
            }
        } else {
            log.error("应用名为null生成应用号失败");
        }
        return null;
    }


    private static String genAppId(String appName) throws NoSuchAlgorithmException {
        if (StringUtils.isBlank(appName)) {
            return null;
        }
        //空格替换为0
        char[] chars = appName.toCharArray();
        for(int i = 0 ; i < chars.length; i++){
            if(chars[i]==32){
                chars[i] = 48;
            }
        }
        String replace = new String(chars);
        int length = replace.length();
        if(length<32){
            //32位系统名称不足补0 + 32位MD5(系统名+版本号)
            replace=fillZero(replace,32-length);
        }else{
            //32位系统名称大于截取前32位 + 32位MD5(系统名+版本号)
            replace = replace.substring(0,32);
        }
        return String.format("%s%s", replace, genAfter(appName));
    }

    //生成32位MD5(系统名+版本号)
    private static String genAfter(String appName) throws NoSuchAlgorithmException {
        String md5 = String.format("%s", appName);
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(md5.getBytes());
        byte[] digest = md.digest();
        int i;
        StringBuffer sbf = new StringBuffer();
        for(int offset = 0 ; offset<digest.length; offset++){
            i=digest[offset];
            if(i<0) {
                i += 256;
            }
            if(i<16){
                sbf.append("0");
            }
            sbf.append(Integer.toHexString(i));
        }
        return sbf.toString();
    }
    //补0
    private static String fillZero(String str,int len){
        StringBuffer sbf = new StringBuffer(str);
        while (len--!=0){
            sbf.append("0");
        }
        return sbf.toString();
    }

    public static void main(String[] args) {
        log.info(generateAppID("app_integrated_manage"));
//        logger.info(generateAppID("test_sys2","v2.0"));
//        DESUtil desObj = new DESUtil();
//        String dec = desObj. strEnc("123qwe", DESUtil.KEY1, "", "");
//        System.out.println(dec);
    }
}
