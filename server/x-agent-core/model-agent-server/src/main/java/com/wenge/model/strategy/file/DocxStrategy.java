package com.wenge.model.strategy.file;

import com.wg.appframe.core.constant.StringConstant;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EmptyFileException;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class DocxStrategy implements FileStrategy {
    private final InputStream inputStream;

    public DocxStrategy(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public InputStream decrypt(String password) throws IOException {
        return null;
    }

    @SneakyThrows
    @Override
    public String getContent() {
        try (BufferedInputStream bis = new BufferedInputStream(inputStream);) {
            XWPFDocument document = new XWPFDocument(bis);
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            return extractor.getText();

        } catch (Exception e) {
            log.info("docx解析失败", e);
            if (!(e instanceof EmptyFileException)) {
                throw new RuntimeException(e);
            }
        } finally {
            inputStream.close();
        }
        return StringConstant.BLANK;
    }
}
