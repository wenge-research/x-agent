package com.wenge.model.strategy.file;

import cn.hutool.core.io.IoUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class DocStrategy implements FileStrategy {
    private final InputStream inputStream;

    public DocStrategy(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public InputStream decrypt(String password) throws IOException {
        Path path = Paths.get("temp.doc");
        try{
            // 暂时返回原来的输入流
            IoUtil.copy(inputStream, Files.newOutputStream(path));
            return Files.newInputStream(path);
        }finally {
            Files.delete(path);
        }
    }

    @SneakyThrows
    @Override
    public String getContent() {
        try {
            HWPFDocument document = new HWPFDocument(new BufferedInputStream(inputStream));
            WordExtractor extractor = new WordExtractor(document);
            return extractor.getText();
        } catch (IOException e) {
            log.info("doc文件解析失败", e);
            throw new RuntimeException(e);
        } finally {
            inputStream.close();
        }
    }
}
