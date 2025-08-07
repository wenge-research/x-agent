package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class FilePageParam extends WgPageInfo {

    private static final long serialVersionUID = -7871101732390296038L;

    /**
     * 文件夹id
     */
    private String folderId;

    /**
     *  知识id
     */
    private String knowledgeId;
    private List<String> knowledgeIds;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件类型
     */
    private List<Integer> fileTypes;


    /**
     * 数据类型
     */
    private String dataType;

    private List<String> fileIds;

    /**
     * 关键词搜索条件(匹配文件以及切片内容)
     */
    private String keywords;
}
