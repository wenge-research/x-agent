/*
 * Copyright 2021-2021 Wenge Group Holding Ltd.
 */

package com.wg.appframe.core.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * <p>
 * AES加解密工具类
 * </p>
 *
 * @author yangyunjun
 * @since 2021-07-08
 */
@Slf4j
public class AESUtils {


    /**
     * 加密方法
     *
     * @param data 要加密的数据
     * @param key  加密key
     * @param iv   加密iv
     * @return 加密的结果
     */
    public static String encrypt(String data, String key, String iv) {
        try {
            if (StrUtil.isBlank(data)) {
                return "";
            }

            // "算法/模式/补码方式"NoPadding PkcsPadding
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            int blockSize = cipher.getBlockSize();

            byte[] dataBytes = data.getBytes();
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }
            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plaintext);
            String encode = Base64.encode(encrypted);
            return encode.trim();
        } catch (Exception e) {
            throw new RuntimeException("encrypt data found error.");
        }
    }

    /**
     * 解密方法
     *
     * @param data 要解密的数据
     * @param key  解密key
     * @param iv   解密iv
     * @return 解密的结果
     */
    public static String desEncrypt(String data, String key, String iv) {
        try {
            if (StrUtil.isBlank(data) || data.equals("undefined") || data.equals("null")) {
                return "";
            }

            // 针对有空格的加密数据，将其还原为 +
            if (data.contains(" ")) {
                data = data.replaceAll(" ", "+");
            }

            byte[] encrypted1 = new Base64().decode(data);
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original).trim();
        } catch (Exception e) {
            log.info("解密失败数据: {}", data);
            throw new RuntimeException("desEncrypt data found error.");
        }
    }

//    /**
//     * 使用默认的key和iv加密
//     *
//     * @param data 数据
//     * @return 返回
//     */
//    public static String encrypt(String data) {
//        return encrypt(data, AES_KEY, AES_IV);
//    }
//
//    /**
//     * 使用默认的key和iv解密
//     *
//     * @param data 加密数据
//     * @return 返回
//     */
//    public static String desEncrypt(String data) {
//        return desEncrypt(data, AES_KEY, AES_IV);
//    }


    /**
     * 测试
     */
    public static void main(String[] args) throws Exception {

        String test1 = "tst";
        String test = new String(test1.getBytes(), "UTF-8");
        String data = null;
        String key = "5555555555555555";
        String iv = "889897779aafa";
        data = encrypt(test, key, iv);
        System.out.println("数据：" + test);
        System.out.println("加密：" + data);
        String jiemi = desEncrypt(data, key, iv).trim();
        System.out.println("解密：" + jiemi);
        System.out.println(desEncrypt("sKy0eVoF5buJITNK5asMQQ==", key, iv));
    }
}
