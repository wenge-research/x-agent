package com.wenge.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/3 18:28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SqlSourceVo {
    /**
     * ip地址
     */
    private String host;
    /**
     * 端口
     */
    private String port;
    /**
     * 数据库用户名
     */
    private String user;
    /**
     * 数据库密码
     */
    private String password;
    /**
     * 数据库名称
     */
    private String database;
    /**
     * jdbc:mysql://等前缀
     */
    private String prefix;

}
