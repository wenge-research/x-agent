package com.wenge.model.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;

public class AudioUtil {

    public static void createAudioFile(String hexString, String outputFilePath) {
        if (StringUtils.isBlank(hexString)) {
            return;
        }

        // 转换为字节数组
        byte[] audioBytes = hexStringToByteArray(hexString);

        // 写入到音频文件
        // String outputFilePath = filePath;
        try {
            writeAudioToFile(outputFilePath, audioBytes);
            System.out.println("音频文件已成功写入: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("写入音频文件时出错: " + e.getMessage());
        }
    }

    /**
     * 将十六进制字符串转换为字节数组
     *
     * @param hexString 十六进制字符串
     * @return 字节数组
     */
    private static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * 将字节数组写入音频文件
     *
     * @param filePath 文件路径
     * @param audioData 音频字节数组
     * @throws IOException IO异常
     */
    public static void writeAudioToFile(String filePath, byte[] audioData) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(audioData);
        }
    }
}
