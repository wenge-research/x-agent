package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SourceAnswerParam extends WGParam {

    private static final long serialVersionUID = 7408849922010567565L;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 对话id
     */
    private String dialogueId;

    /**
     * 是否来自管理端,1-是
     */
    private String fromManage;

    /**
     * 智能搜索模块  搜索文件类型，-1或不传-全部（默认值）、0-文件、1-音频、2-图片、3-视频，
     * 注意：该字段请勿传null
     */
    private Integer searchType = -1;

    /**
     * 相关度排序标识: 0-降序（默认值），1-升序
     * 注意：该字段请勿传null
     */
    private Integer relevance = 0;
}
