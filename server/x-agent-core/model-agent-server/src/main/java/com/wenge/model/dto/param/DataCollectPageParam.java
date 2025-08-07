package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataCollectPageParam extends WgPageInfo {

    private static final long serialVersionUID = 680162544635417399L;

    /**
     * 数据集名称
     */
    private String name;

    /**
     * 标签
     */
    private String tag;
}
