package com.wenge.model.utils;


import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Locale;


public class GkDesUtil {

    /**
     * DES解密
     *
     * @param secretData 密码字符串
     * @param secretKey  解密密钥
     * @return 原始字符串
     * @throws Exception
     */
    private static final String DES_ALGORITHM = "DES";

    public static String decryption(String secretData, String secretKey) throws Exception {
        if (secretData == null) {
            return null;
        }
        //表示为ios加密
        if(secretData.startsWith("ios:")){
            secretData = secretData.split(":")[1];
            return decrypt(secretData,secretKey);
        }else{
            //其他加密
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance("DES/ECB/NoPadding");
                cipher.init(Cipher.DECRYPT_MODE, generateKey(secretKey));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                throw new Exception("NoSuchAlgorithmException", e);
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
                throw new Exception("NoSuchPaddingException", e);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                byte[] buf = cipher.doFinal(hexStr2Bytes(secretData));
                int num = 0;
                for (byte b: buf) {
                    String name = b+"";
                    if (name.length() == 1) {
                        num++;
                    }
                }

                byte[] bytes = new byte[buf.length-num];
                for (int i =0; i < buf.length; i++) {
                    String name = buf[i]+"";
                    if (name.length() != 1) {
                        bytes[i] = buf[i];
                    }
                }

                return new String(bytes, "utf-8");

            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("IllegalBlockSizeException", e);
            }
        }
    }

    public static byte[] hexStr2Bytes(String src) {
        /*对输入值进行规范化整理*/
        src = src.trim().replace(" ", "").toUpperCase(Locale.US);
        //处理值初始化
        int m = 0, n = 0;
        int iLen = src.length() / 2; //计算长度
        byte[] ret = new byte[iLen]; //分配存储空间

        for (int i = 0; i < iLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = (byte) (Integer.decode("0X" + src.substring(i * 2, m) + src.substring(m, n)) & 0xFF);
        }
        return ret;
    }
    /**
     * 获得秘密密钥
     *
     * @param secretKey
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static SecretKey generateKey(String secretKey)
            throws Exception {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES_ALGORITHM);
        DESKeySpec keySpec = new DESKeySpec(secretKey.getBytes());
        keyFactory.generateSecret(keySpec);
        return keyFactory.generateSecret(keySpec);
    }
    public static String decrypt(String data, String key) throws IOException,
            Exception {
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] buf = decoder.decodeBuffer(data);
        byte[] bt = decrypt(buf,key.getBytes());
        return new String(bt);
    }
    /**
     * Description 根据键值进行解密
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
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES_ALGORITHM);
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES_ALGORITHM);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

        return cipher.doFinal(data);
    }

    /**
     * DES算法，加密
     *
     * @param data 待加密字符串
     * @param key  加密私钥，长度不能够小于8位
     * @return 加密后的字节数组，一般结合Base64编码使用
     */
    public static String encode(String data,String key) throws Exception{
        SecretKeySpec key0 = new SecretKeySpec(getKey(key), "DES");
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key0);
        byte[] encryptedData = cipher.doFinal(padding(data));
        return bytesToHexString(encryptedData);
    }
    public static byte[] getKey(String keyRule) {
        Key key = null;
        byte[] keyByte = keyRule.getBytes();
        // 创建一个空的八位数组,默认情况下为0
        byte[] byteTemp = new byte[8];
        // 将用户指定的规则转换成八位数组
        for (int i = 0; i < byteTemp.length && i < keyByte.length; i++) {
            byteTemp[i] = keyByte[i];
        }
        key = new SecretKeySpec(byteTemp, "DES");
        return key.getEncoded();
    }

    public static byte[] padding(String arg_text){
        byte[] encrypt = arg_text.getBytes();

        if(encrypt.length % 8 != 0){ //not a multiple of 8
            byte[] padded = new byte[encrypt.length + 8 - (encrypt.length % 8)];

            //copy the old array into it
            System.arraycopy(encrypt, 0, padded, 0, encrypt.length);
            encrypt = padded;
        }
        return encrypt;
    }
    /**
     * 把字节数组转换成16进制字符串
     *
     * @param bArray
     * @return
     */
    public static final String bytesToHexString(byte[] bArray) {
        if(bArray == null )
        {
            return "";
        }
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }
    public static void main(String[] args) throws Exception {
        String appkey="sprint-ucm";
        String appSecret="bbbbbbaaaaaaaaaaaaa";
        Long timeStamp = System.currentTimeMillis();

        String signatures = encode(timeStamp + appkey, appSecret);
        System.out.println("key:"+appkey);
        System.out.println("timeStamp:"+timeStamp);
        System.out.println("signatures:"+signatures);

        String decryptData = decryption(signatures, appSecret);
        System.out.println("decryptData:" + decryptData);
    }
}