package com.wenge.model.dto.param.wenhai;

import java.util.List;

/**
 * SimpleKeyword, 简易模式【searchType=1，此字段必填】</br><span style='color:red;'>注意</span>：1. [
 * !#^&*+|(){}[]<>~！#&*（）——+|{}【】]-等特殊符号不支持；</br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.不支持如!&,!|,!!
 * 连着写法；</br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3.三组关键词累加输入长度限制不超过200(包含200)。
 */
@lombok.Data
public class SimpleKeyword {
    /**
     * 任意包含
     */
    private List<String> anyKeyWord;
    /**
     * 必须包含
     */
    private List<String> mustKeyWord;
    /**
     * 必须包含【繁体】
     */
    private List<String> mustKeyWordComplex;
    /**
     * 需要排除
     */
    private List<String> needExclude;
}
