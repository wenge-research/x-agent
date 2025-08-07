package com.wenge.model.dto.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectFileTable implements Serializable {

    private static final long serialVersionUID = -1981711148770029605L;

    /**
     * 数据 host
     */
    private String host;

    /**
     * 数据库 端口
     */
    private String port;

    /**
     * 数据库名称
     */
    private String database;

    /**
     * 数据库用户名
     */
    private String username;

    /**
     * 数据库密码，AES 加密
     */
    private String pwkey;

    /**
     * 数据库表名
     */
    private String tableName;

}
