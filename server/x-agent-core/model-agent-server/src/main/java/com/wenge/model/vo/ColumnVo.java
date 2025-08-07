package com.wenge.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/11 11:38
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColumnVo {
    private String columnName;
    private Object columnValue;
}