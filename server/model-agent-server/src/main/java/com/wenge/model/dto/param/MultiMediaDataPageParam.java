package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MultiMediaDataPageParam extends WgPageInfo {

    private static final long serialVersionUID = 3514272693195916373L;

    private String multiMediaId;

    private String content;
}
