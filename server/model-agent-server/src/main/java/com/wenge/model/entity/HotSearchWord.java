package com.wenge.model.entity;

import com.mybatisflex.annotation.Table;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@ApiModel
@Data
@Table(value = "hot_search_word", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class HotSearchWord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String applicationId;

    // 热搜词
    private String keyword;
    // 搜索次数
    private Integer count;
    // 热搜词拼音
    private String wordPy;
    // 热搜词拼音首字母缩写
    private String wordFirstPy;



}
