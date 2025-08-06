package com.wenge.oauth.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CacheClearParam extends WGParam {

    private static final long serialVersionUID = -4920328650522215036L;

    /**
     * 来源于
     */
    private String fromNode;
}
