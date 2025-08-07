package com.wenge.model.dto.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TableResult implements Serializable {

    private static final long serialVersionUID = 6040470330188923306L;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表结构
     */
    private String tableDdl;

    /**
     * 字段列表
     */
    private List<Column> columnList;

    @Data
    public static class Column implements Serializable {

        private static final long serialVersionUID = -4490866437122779351L;

        private String columnName;
        private String columnType;
    }
}
