package com.wg.appframe.elasticsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: ES文档数据结构
 * @Author: zoupan
 * @CreateTime: 2021-12-27 18:18:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsDocumentVo {
    /**
     * 索引名称
     */
    private String index;

    /**
     * 文档ID
     */
    private String documentId;

    /**
     * 文档类型
     */
    private String type;

    /**
     * json格式文档对象
     */
    private Object jsonDocument;
}
