package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class FoldersDeleteParam extends WGParam {

    private static final long serialVersionUID = 3540044925967238796L;

    /**
     * 文件夹id
     */
    private List<String> foldersIdList;

    /**
     * 知识库id
     */
    private String knowledgeId;
}
