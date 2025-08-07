package com.wg.appframe.core.utils;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 各项数据规则限定
 *
 * @author Administrator
 */
public class ValidatorUtils {
    
    /**
     * 手机号验证
     *
     * @param phone
     * @return 验证通过返回true
     */
    public static boolean isMobile(String phone) {
        if (StringUtils.isBlank(phone)) {
            return false;
        }
        
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        if (phone.length() != 11) {
            return false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            boolean isMatch = m.matches();
            return isMatch;
        }
    }
    
    /**
     * 校验 营业执照注册号
     *
     * @param businesslicense
     * @return
     */
    public static boolean isLicense_15(String businesslicense) {
        if ("".equals(businesslicense) || " ".equals(businesslicense)) {
            return false;
        } else if (businesslicense.length() != 15) {
            return false;
        }
        String businesslicensePrex14 = businesslicense.substring(0, 14);// 获取营业执照注册号前14位数字用来计算校验码
        String businesslicense15 = businesslicense.substring(14, businesslicense.length());// 获取营业执照号的校验码
        char[] chars = businesslicensePrex14.toCharArray();
        int[] ints = new int[chars.length];
        for (int i = 0; i < chars.length; i++) {
            ints[i] = Integer.parseInt(String.valueOf(chars[i]));
        }
        getCheckCode(ints);
        // 比较填写的营业执照注册号的校验码和计算的校验码是否一致
        return businesslicense15.equals(getCheckCode(ints) + "");
    }
    
    
    /**
     * 获取 营业执照注册号的校验码
     *
     * @param ints
     * @return
     */
    private static int getCheckCode(int[] ints) {
        if (null != ints && ints.length > 1) {
            int ti = 0;
            int si = 0;// pi|11+ti
            int cj = 0;// （si||10==0？10：si||10）*2
            int pj = 10;// pj=cj|11==0?10:cj|11
            for (int i = 0; i < ints.length; i++) {
                ti = ints[i];
                pj = (cj % 11) == 0 ? 10 : (cj % 11);
                si = pj + ti;
                cj = (0 == si % 10 ? 10 : si % 10) * 2;
                if (i == ints.length - 1) {
                    pj = (cj % 11) == 0 ? 10 : (cj % 11);
                    return pj == 1 ? 1 : 11 - pj;
                }
            }
        }
        return -1;
        
        
    }
    
    /**
     * 营业执照 统一社会信用代码（18位）
     *
     * @param license
     * @return
     */
    public static boolean isLicense18(String license) {
        if (StringUtils.isBlank(license)) {
            return false;
        }
        if (license.length() != 18) {
            return false;
        }
        
        //如果开头8位数不是91440300
        if (!license.subSequence(0, 8).equals("91440300")) {
            return false;
        }
        String regex = "^([159Y]{1})([1239]{1})([0-9ABCDEFGHJKLMNPQRTUWXY]{6})([0-9ABCDEFGHJKLMNPQRTUWXY]{9})([0-90-9ABCDEFGHJKLMNPQRTUWXY])$";
        if (!license.matches(regex)) {
            return false;
        }
        String str = "0123456789ABCDEFGHJKLMNPQRTUWXY";
        int[] ws = {1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28};
        String[] codes = new String[2];
        codes[0] = license.substring(0, license.length() - 1);
        codes[1] = license.substring(license.length() - 1, license.length());
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += str.indexOf(codes[0].charAt(i)) * ws[i];
        }
        int c18 = 31 - (sum % 31);
        if (c18 == 31) {
            c18 = 30;
        }
        if (str.charAt(c18) != codes[1].charAt(0)) {
            return false;
        }
        return true;
    }
    
    /**
     * 身份证号脱敏（保留前六后三）
     *
     * @param idNumber
     * @return
     */
    public static String desensitizedIdNumber(String idNumber) {
        if (!Strings.isNullOrEmpty(idNumber)) {
            idNumber = idNumber.trim();
            if (idNumber.length() == 15) {
                //idNumber = idNumber.replaceAll("(\\w{6})\\w*(\\w{4})", "$1******$2");
                idNumber = idNumber.substring(0, 6) + "******" + idNumber.substring(idNumber.length() - 4);
            } else if (idNumber.length() == 9) {
                //idNumber = idNumber.replaceAll("(\\w{3})\\w*(\\w{3})", "$1******$2");
                idNumber = idNumber.substring(0, 3) + "******" + idNumber.substring(idNumber.length() - 3);
            } else if (idNumber.length() == 18) {
                //入境登记35032219​8109021536脱敏会失效-202004091647
                //idNumber = idNumber.replaceAll("(\\w{6})\\w*(\\w{4})", "$1********$2");
                idNumber = idNumber.substring(0, 6) + "********" + idNumber.substring(idNumber.length() - 4);
            } else {
                idNumber = idNumber.replaceAll("(\\w{3})\\w*(\\w{3})", "$1******$2");
            }
        }
        return idNumber;
    }
    
}
