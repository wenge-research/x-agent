package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class KnnBindParam extends WGParam {

    private static final long serialVersionUID = -6031367684882983512L;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 知识库 id
     */
    private String knnId;
}
