package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询分组与事项的关联关系
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SceneMatterGroupRefListParam extends WGParam {

    private static final long serialVersionUID = -7965325983023452518L;

    /**
     * 场景id
     */
    private String sceneId;
}
