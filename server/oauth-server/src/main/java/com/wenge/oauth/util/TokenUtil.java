package com.wenge.oauth.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;  
import javax.crypto.SecretKey;  
import javax.crypto.spec.SecretKeySpec;  
import java.nio.charset.StandardCharsets;  
import java.util.Base64;  
  
public class TokenUtil {  
  
    // AES密钥（实际应用中应从安全存储中加载密钥）  
    private static final String SECRET_KEY = "QWERTYUIOPASDFGHJKLZXCVBNMQWERTY"; // 32字节密钥

    public static final String SIGN = "#$#";
  
    // 生成一个256位的AES密钥（示例）  
    private static SecretKey generateSecretKey() throws Exception {  
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");  
        keyGen.init(64);
        return keyGen.generateKey();  
    }  
  
    // 初始化密钥（使用静态密钥作为示例）  
    private static SecretKeySpec getSecretKey() {  
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);  
        return new SecretKeySpec(keyBytes, "AES");  
    }

    // 生成token  
    public static String generateToken(String modelPluginApiId, String id, String name, String phone, String expire_time) throws Exception {
        String input = modelPluginApiId + "@" + id + "@" + name + "@" + phone + "@" + expire_time;
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
        byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
        return SIGN + Base64.getEncoder().encodeToString(encrypted);
    }
  
    // 解析token  
    public static String[] parseToken(String token) throws Exception {  
        Cipher cipher = Cipher.getInstance("AES");  
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
        byte[] decodedBytes = Base64.getDecoder().decode(token.replace(SIGN, ""));
        byte[] decrypted = cipher.doFinal(decodedBytes);  
        return new String(decrypted, StandardCharsets.UTF_8).split("@");
    }
  
    public static void main(String[] args) {  
        try {
            String modelPluginApiId = "fb88858fbf3b4405b14acbec7e2b7b2b";
            String name = "王五";
            String phone = "15242125236";
            String id = "20";
            String expire_time = "2024-11-19 00:00:00";

            // 生成token  
            String token = generateToken(modelPluginApiId,id, name, phone, expire_time);
            System.out.println("Generated Token: " + token);  
  
            // 解析token  
            String[] parsedData = parseToken(token);
            for (int i = 0; i < parsedData.length; i++) {
                System.out.println("Parsed Data " + (i+1) + ": " + parsedData[i]);
            }
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}