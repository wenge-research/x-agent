package com.wg.appframe.wos.dto.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class MinioInfoResult implements Serializable {

    private static final long serialVersionUID = -468193851977967103L;
    
    /**
     * 主键ID
     */
    private String id;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * minio保存文件标识符
     */
    private String fileStoreKey;

    /**
     * 文件大小(kb)
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件地址
     */
    private String url;

    /**
     * 文件路径
     */
    private String urlPath;

    /**
     * 文件地址
     */
    private String minioUrl;

    /**
     * 最近修改时间
     */
    private String lastModified;

    
}
