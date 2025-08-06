package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class McpFindParam extends WGParam {

    private static final long serialVersionUID = 8602878171385243928L;

    /**
     * mcp名称
     */
    private String mcpName;

}
