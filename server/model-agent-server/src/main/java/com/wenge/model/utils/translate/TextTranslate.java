package com.wenge.model.utils.translate;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author:caohaifeng
 * @createTime:2024-09-24 17:11
 * @Description: txt翻译类
 * @Version:1.0
 */
public class TextTranslate {
    /**
     * 翻译 txt文档
     **/
    public static void translateTxt(InputStream inputStream, String fileUrl, String srcLang, String tgtLang) {
        List<String> lines = new ArrayList<>();
        List<String> translateLines = new ArrayList<>();
        try {
//            lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
            byte[] buffer = new byte[1024]; // 创建一个缓冲区
            int length;
            // 读取源文件并写入到目标文件
            while ((length = inputStream.read(buffer)) > 0) {
                String content = new String(buffer);
                lines.add(content);
            }
            System.out.println("文件读取完成");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (lines!=null) {
            lines.forEach(str->{
                String translatedText = TranslateUtils.getTranslateContent(str, srcLang, tgtLang);
                translateLines.add(translatedText);
            });
        }
        String content = String.join("\n", translateLines);
        try {
            Files.write(Paths.get(fileUrl), content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
