package com.wenge.model.dto.param.wenhai;

import java.util.List;

/**
 * SearchMode, 检索模式【如果searchType=0，keywordExps必填;如果searchType=1，simpleKeyword必填】
 */
@lombok.Data
public class SearchMode {
    /**
     * 高级模式-表达式【searchType=0，此字段必填】</br><span
     * style='color:red;'>注意</span>：1.关键词输入长度限制不超过200(包含200、&|!表达式括弧都算)；</br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.不支持如!&,!|,!!
     * 连着写法；</br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3.不支持多于一层的括号,错误示例：(中国&(北京|上海)),会解析错误；</br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4.关键词不需要被引号引用,错误示例：("北京")；</br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;5.排除逻辑!,!后跟随的括号为排除的关键词,无论里面输入的是与还是或,都是或的关系,如:!(北京&上海)会被转为!(北京|上海)。
     */
    private List<String> keywordExps;
    /**
     * 查询方式,默认模糊查找、true-精确、false-模糊
     */
    private Boolean queryType;
    /**
     * 搜索关键词类型【默认高级模式,0-高级模式、1-简易模式】
     */
    private Long searchType;
    private SimpleKeyword simpleKeyword;
}
