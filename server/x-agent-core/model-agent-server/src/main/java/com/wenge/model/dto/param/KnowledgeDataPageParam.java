package com.wenge.model.dto.param;

import com.mybatisflex.annotation.Column;
import com.wg.appframe.core.bean.WgPageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class KnowledgeDataPageParam extends WgPageInfo {

    private static final long serialVersionUID = -7654770951417312708L;

    private String knowledgeId;
    private List<String> knowledgeIds;

    /**
     * 内容
     */
    private String content;

    /**
     * 标题
     */
    private String title;

    /**
     * 是否精确回答，是/否
     */
    private String accurate;

    /**
     * 数据分类
     */
    private String category;

    private boolean needParam = false;


    /**
     * 问答对对比相似度阀值
     */
    @ApiModelProperty(name = "qaSimilarityScoreMin", value = "问答对对比相似度阀值", dataType = "Float")
    private Float qaSimilarityScoreMin;

    /**
     * 问答对对比相似度阀值
     */
    @ApiModelProperty(name = "qaSimilarityScoreMax", value = "问答对对比相似度阀值", dataType = "Float")
    private Float qaSimilarityScoreMax;


    /**
     * 开始时间
     */
    private String updateTimeStart;
    /**
     * 结束时间
     */
    private String updateTimeEnd;

}
