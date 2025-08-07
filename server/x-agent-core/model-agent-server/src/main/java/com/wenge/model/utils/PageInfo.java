package com.wenge.model.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @Author: lwq
 * @Date: 2024/7/21 17:43
 * @Desc:
 **/
@Data
@AllArgsConstructor
public class PageInfo<T> {

    /**
     * 分页查询的参数，当前页数
     */
    private long current;
    /**
     * 分页查询的参数，当前页面每页显示的数量
     */
    private long size;

    private long total;

    private List<T> records;

}
