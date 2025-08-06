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
public class ExcelInfoVo {
    private String columnName;
    private String excelName;
    private int index;
}