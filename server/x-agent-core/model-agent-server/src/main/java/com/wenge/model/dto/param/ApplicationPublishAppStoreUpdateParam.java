package com.wenge.model.dto.param;

import lombok.Data;

@Data
public class ApplicationPublishAppStoreUpdateParam {

    private static final long serialVersionUID = -2636192558690322065L;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 是否发布应用商店 0-未发布（下架） 1-已发布（上架） 2-待审核
     */
    private Integer publishAppStore;

}
