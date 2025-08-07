package com.wenge.model.strategy.file;

import java.io.InputStream;

/**
 * 文件策略工厂类
 */
public class FileStrategyFactory {

    public static FileStrategy createStrategy(InputStream inputStream, String fileType) {
        switch (fileType) {
            case "pdf":
                return new PdfStrategy(inputStream);
            case "doc":
                return new DocStrategy(inputStream);
            case "docx":
                return new DocxStrategy(inputStream);
            case "json":
                return new JsonStrategy(inputStream);
            case "txt":
                return new TxtStrategy(inputStream);
            default:
                return null;
        }
    }
}
