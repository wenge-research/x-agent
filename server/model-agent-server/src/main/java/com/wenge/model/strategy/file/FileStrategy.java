package com.wenge.model.strategy.file;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文件策略接口
 */
public interface FileStrategy {

    /**
     * 设置文件为非加密
     */
    InputStream decrypt(String password) throws IOException;

    /**
     * 获取文件内容
     */
    String getContent();

}
