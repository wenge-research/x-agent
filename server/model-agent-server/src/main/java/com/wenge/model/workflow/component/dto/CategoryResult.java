package com.wenge.model.workflow.component.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CategoryResult implements Serializable {

    private static final long serialVersionUID = -4756256521483638403L;

    /**
     * 关键词
     */
    private String keywords;

    /**
     * 类别id
     */
    private String categoryId;

    /**
     * 类别名称
     */
    private String categoryName;
}
