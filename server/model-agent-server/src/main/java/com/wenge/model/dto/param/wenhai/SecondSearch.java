package com.wenge.model.dto.param.wenhai;

/**
 * SecondSearch, 二次搜索对象
 */
@lombok.Data
public class SecondSearch {
    /**
     * 匹配字段【默认正文匹配,
     * 2-OCR、3-语音、5-全文(标题+正文+OCR+语音)、6-标题、7-正文(内容)、8-发布平台(站点名称暂不支持模糊匹配)、9-作者、10-频道名称】
     */
    private Long matchMethod;
    /**
     * 查询方式,默认精确查找、true-精确、false-模糊
     */
    private Boolean queryType;
    /**
     * 二次检索值【空格支持多组搜索】
     */
    private String value;
}
