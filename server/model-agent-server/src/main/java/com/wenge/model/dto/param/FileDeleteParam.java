package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class FileDeleteParam extends WGParam {

    private static final long serialVersionUID = 5843737233289631006L;

    /**
     * 知识库id
     */
    private String knowledgeId;

    /**
     * 文件id
     */
    private List<String> idList;

    /**
     * 文件夹id
     */
    private List<String> foldersIdList;
}
