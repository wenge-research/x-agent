package com.wenge.model.dto.result;

import com.wg.appframe.core.dto.results.WGResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AiImageResult extends WGResult {

    private static final long serialVersionUID = 8354076789866858710L;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 比例
     */
    private String ratio;

    /**
     * 图片生成描述
     */
    private String description;

    /**
     * 宽
     */
    private Integer width;

    /**
     * 高
     */
    private Integer height;

}
