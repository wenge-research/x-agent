/*
 * Copyright 2021-2021 Wenge Group Holding Ltd.
 */

package com.wg.appframe.core.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 分页公共Bean
 * </p>
 *
 * @author wangxingpeng
 * @since 2020-08-26
 */
@Data
public class WgPageInfo implements Serializable {

    private static final long serialVersionUID = -5941444087556983345L;

    /**
     * 分页开始页
     */
    private Integer pageNo;

    /**
     * 分页数据量
     */
    private Integer pageSize;

    /**
     * 排序字段
     */
    private String order;

    /**
     * 排序规则
     */
    private String sort;

}
