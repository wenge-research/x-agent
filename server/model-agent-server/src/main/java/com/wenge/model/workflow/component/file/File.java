package com.wenge.model.workflow.component.file;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
@Data
public class File {
    /**
     * 文件id
     */
    private String id;
    /**
     * 文件类型
     */
    private FileType type;
    /**
     * 文件大小
     */
    private Integer size;
    /**
     * 文件传输方式
     */
    private FileTransferMethod transferMethod;
    /**
     * 远程文件地址
     */
    private String remoteUrl;
    /**
     * 文件名
     */
    private String filename;
    /**
     * 文件拓展名
     */
    private String extension;
    /**
     * 文件媒体类型
     */
    private String mimeType;

    public String getField(String fieldName) {
        try {
            Field field = this.getClass().getDeclaredField(fieldName);
            return (String) field.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.info("试图查询: {}字段, 但该字段不在File中", fieldName);
            throw new RuntimeException(e);
        }
    }
}
