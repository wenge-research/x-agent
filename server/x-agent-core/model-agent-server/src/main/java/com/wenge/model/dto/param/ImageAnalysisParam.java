package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImageAnalysisParam extends WGParam {

    private static final long serialVersionUID = 5843737233289631006L;

    /**
     * 图片url列表
     */
    private List<String> imageUrlList;
}
