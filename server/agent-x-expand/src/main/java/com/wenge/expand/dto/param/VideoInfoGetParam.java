package com.wenge.expand.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class VideoInfoGetParam extends WGParam {

    private static final long serialVersionUID = 5813171118725395988L;

    /**
     * 视频地址
     */
    private String videoUrl;

    /**
     * 封面地址
     */
    private String coverUrl;
}
