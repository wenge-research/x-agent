package com.wenge.model.strategy.file;


import cn.hutool.core.util.StrUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PdfStrategy implements FileStrategy {
    private static final Logger log = LoggerFactory.getLogger(PdfStrategy.class);
    private final InputStream inputStream;

    public PdfStrategy(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public InputStream decrypt(String password) throws IOException {
        Path path = Paths.get("temp.pdf");
        BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(path));
        // 如果密码为空，不使用密码打开，如果报错，则是加密文件，返回原输入流
        if (StrUtil.isBlank(password)) {
            try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputStream), new PdfWriter(outputStream))) {
                return Files.newInputStream(path);
            } finally {
                Files.delete(path);
            }
        }
        // 如果密码不为空，使用密码打开，如果报错，则密码错误，返回新输入流
        try (PdfDocument document = new PdfDocument(
                new PdfReader(inputStream, new ReaderProperties().setPassword(password.getBytes())),
                new PdfWriter(outputStream))) {
            return Files.newInputStream(path);
        } finally {
            Files.delete(path);
        }
    }

    @SneakyThrows
    @Override
    public String getContent() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputStream))) {

            int numberOfPages = pdfDoc.getNumberOfPages();
            StringBuilder content = new StringBuilder();

            // 遍历每一页并提取文本
            for (int i = 1; i <= numberOfPages; i++) {
                String pageContent = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i));
                content.append(pageContent).append("\n");
            }

            return content.toString();
        } catch (Exception e) {
            log.info("pdf解析失败", e);
            throw new RuntimeException(e);
        } finally {
            inputStream.close();
        }
    }
}
