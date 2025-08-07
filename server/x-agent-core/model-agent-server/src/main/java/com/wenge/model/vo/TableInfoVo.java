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
public class TableInfoVo {
    private String columnName;
    private String columnType;
    private int columnSize;
    private boolean isNullable;
    private String remarks;
    // 1未勾选 2已勾选
    private Integer flag;
}