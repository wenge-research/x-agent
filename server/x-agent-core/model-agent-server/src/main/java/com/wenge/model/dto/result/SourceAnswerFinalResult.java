package com.wenge.model.dto.result;

import com.wg.appframe.core.dto.results.WGResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SourceAnswerFinalResult extends WGResult {

    private static final long serialVersionUID = -6415080641520901306L;

    /**
     * 共查找的文档总数
     */
    private Long fileTotalCount;

    /**
     * 命中内容的文档数
     */
    private Long selectedFileCount;

    /**
     * 是否开启联网判断标识 0-否 1-是
     */
    private Integer networkCheckFlag;

    /**
     * 答案溯源结果
     */
    List<SourceAnswerResult> sourceAnswerResultList;

}
