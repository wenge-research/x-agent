package com.wenge.model.strategy.file;

import com.wg.appframe.core.constant.StringConstant;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EmptyFileException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
public class JsonStrategy implements FileStrategy {
    private final InputStream inputStream;

    public JsonStrategy(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public InputStream decrypt(String password) throws IOException {
        return null;
    }

    @SneakyThrows
    @Override
    public String getContent() {
        try  {
            return new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

        } catch (Exception e) {
            log.info("json解析失败", e);
            if (!(e instanceof EmptyFileException)) {
                throw new RuntimeException(e);
            }
        } finally {
            inputStream.close();
        }
        return StringConstant.BLANK;
    }
}
