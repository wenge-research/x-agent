package com.wenge.model.utils;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/6/20 9:44
 */

public class FileUtils {

    public static MultipartFile str2MultipartFile(String str) throws IOException {
        // 将字符串内容转换为字节数组输入流
        InputStream inputStream = new ByteArrayInputStream(str.getBytes());

        // 创建一个MockMultipartFile对象，模拟上传文件
        MultipartFile multipartFile = new MockMultipartFile("lwq.txt", "lwq.txt", null, inputStream);
        return multipartFile;
    }

    public static void main(String[] args) {
        String content = "这是一个测试文件的内容。";
        try {
            MultipartFile multipartFile = str2MultipartFile(content);

            System.out.println("转换后的MultipartFile对象：" + multipartFile.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}