package com.wenge.model.dto.param.wenhai;

/**
 * FilterSearch, 指定字段过滤值【<span style='color:red;'>注意</span>：field和value两个字段都不为空，才会生效】
 */
@lombok.Data
public class FilterSearch {
    /**
     * 字段类型,空不处理、1-站点名称、2-热点词语、5-专家观点、6-网民观点、19-热门组织、20-热门人物、21-热点短语、34-微博话题
     */
    private Long field;
    /**
     * 检索值
     */
    private String value;
}
