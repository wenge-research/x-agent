/*
 * Copyright 2021-2021 Wenge Group Holding Ltd.
 */

package com.wg.appframe.core.utils;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

/**
 * <p>
 * MD5工具
 * </p>
 *
 * @author yaohangliang
 * @since 2021-11-25
 */
@Slf4j
public class MD5Utils {
    
    private static final String MD5_ALGORITHM = "MD5";
    
    protected static MessageDigest messagedigest = null;

    private static final String DES = "DES";
    
    private static final String KEY = "op2e3d597ae2a1d32s55adku33d53g484591dadbc382a18340bf834";
    
    private static MessageDigest digest;
    
    static {
        try {
            digest = MessageDigest.getInstance(MD5_ALGORITHM);
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }
    
    public static synchronized String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        byte[] bytes = digest.digest(key.getBytes());
        StringBuilder output = new StringBuilder(bytes.length);
        for (Byte entry : bytes) {
            output.append(String.format("%02x", entry));
        }
        digest.reset();
        return output.toString();
    }
    
    /**
     * MD5加密算法
     *
     * @param data
     * @return
     */
    public static String md5Encrypt(String data) {
        String resultString = null;
        try {
            resultString = byte2hexString(digest.digest(data.getBytes()));
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return resultString;
    }
    
    
    private static String byte2hexString(byte[] bytes) {
        StringBuilder bf = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            if ((bytes[i] & 0xff) < 0x10) {
                bf.append("T0");
            }
            bf.append(Long.toString(bytes[i] & 0xff, 16));
        }
        return bf.toString();
    }
    
    /**
     * Description 根据键值进行加密
     *
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    public static String desEncrypt(String data, String key) {
        if (key == null) {
            key = KEY;
        }
        byte[] bt = encrypt(data.getBytes(), key.getBytes());
        
        return Base64Encoder.encode(bt);
    }
    
    /**
     * Description 根据键值进行解密
     *
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static String desDecrypt(String data, String key) throws Exception {
        if (StringUtils.isBlank(data)) {
            return null;
        }
        if (key == null) {
            key = KEY;
        }
        
        byte[] buf = Base64Decoder.decode(data);
        byte[] bt = decrypt(buf, key.getBytes());
        return new String(bt);
    }
    
    /**
     * Description 根据键值进行加密
     *
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] encrypt(byte[] data, byte[] key) {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        byte[] bytes = null;
        try {
            // 从原始密钥数据创建DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(key);
            
            // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            
            SecretKey securekey = keyFactory.generateSecret(dks);
            
            // Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance(DES);
            
            // 用密钥初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
            bytes = cipher.doFinal(data);
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return bytes;
    }
    
    
    /**
     * Description 根据键值进行解密
     *
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        
        SecretKey securekey = keyFactory.generateSecret(dks);
        
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);
        
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
        return cipher.doFinal(data);
    }
    
    //加密
    public static String base64Encrypt(String s) {
        final Base64.Encoder encoder = Base64.getEncoder();
        final String text = s;
        byte[] textByte;
        try {
            textByte = text.getBytes("UTF-8");
            return encoder.encodeToString(textByte);
        } catch (UnsupportedEncodingException e) {
            log.error(e.toString(), e);
        }
        //编码
        return text;
    }
    
    //解密
    public static String base64Decrypt(String encodedText) {
        final Base64.Decoder decoder = Base64.getDecoder();
        //解码
        return new String(decoder.decode(encodedText), StandardCharsets.UTF_8);
    }
    
    
    //创建一个日期加密串
    public static String getMD5String(String baseURL) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String string = baseURL + format.format(new Date());
        return MD5Utils.md5(string);
    }
    

    /**
     * 获取MD5
     *
     * @param str 字符
     * @return 返回
     */
    public static String getMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    /**
     * MD5加密算法
     *
     * @param data data
     * @return 返回
     */
    public static String encryptMD5(String data) {
        StringBuilder result = new StringBuilder();
        digest.update(data.getBytes());
        byte[] bytes = digest.digest();
    
        // 16进制数字
        result = new StringBuilder(new BigInteger(1, bytes).toString(16));
        // 如果生成数字未满32位，需要前面补0
        for (int idx = 0; idx < 32 - result.length(); idx++) {
            result.insert(0, "0");
        }
        return result.toString();
    }

    
    public static void main(String[] args) {
        //"6d38bd08a23e2db471ea89e9176f27b1";
        String string = encryptMD5("system_manage");
        System.out.println(string);
    }

}
