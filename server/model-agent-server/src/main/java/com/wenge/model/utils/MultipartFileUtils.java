package com.wenge.model.utils;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MultipartFileUtils {

    /**
     * @param text 文本内容
     * @param fileName 文件名
     * @return MultipartFile
     * 将文本转成 MultipartFile 文件
     **/
    public static MultipartFile convertTxtToHtml(String text, String fileName) throws IOException {
        File tempFile = File.createTempFile(fileName, ".html");
        tempFile.deleteOnExit();

        // 将文本内容写入临时文件
        Files.write(tempFile.toPath(), text.getBytes());

        return new MockMultipartFile(
                fileName,
                tempFile.getName(),
                "text/html",
                Files.readAllBytes(tempFile.toPath())
        );
    }
}
