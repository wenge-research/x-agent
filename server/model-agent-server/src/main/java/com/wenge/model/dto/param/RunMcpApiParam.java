package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class RunMcpApiParam extends WGParam {

    private static final long serialVersionUID = -6314253313516053444L;

    /**
     * 查询文本
     */
    private String query;

    /**
     * mcp服务id集合
     */
    private List<String> mcpServerIdList;
}
