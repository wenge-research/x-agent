package com.wenge.model.dto.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectField implements Serializable {

    private static final long serialVersionUID = -4332972661485639441L;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 字段描述
     */
    private String fieldDesc;

    /**
     * 字段标题名称
     */
    private String titleName;

    /**
     * 数据示例
     */
    private String example;
}
