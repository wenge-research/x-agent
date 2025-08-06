package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DecisionsReportParam extends WGParam {

    private static final long serialVersionUID = -9120672628565071327L;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 会话id
     */
    private String sessionId;

    /**
     * 标题
     */
    private String title;

    /**
     * 客户id
     */
    private String clientId;
}
