package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class KnowledgeApiRequestRecordPageQueryParam extends WgPageInfo {

    private static final long serialVersionUID = 3037384264619002498L;

    /**
     * 开始日期（格式： yyyy-MM-dd HH:mm:ss）
     */
    private String startTime;

    /**
     * 结束日期（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String endTime;

    /**
     * api业务id
     */
    private String knowledgeApiId;

}
