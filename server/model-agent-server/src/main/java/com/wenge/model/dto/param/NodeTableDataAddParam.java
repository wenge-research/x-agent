package com.wenge.model.dto.param;

import cn.hutool.json.JSONObject;
import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class NodeTableDataAddParam extends WGParam {

    private static final long serialVersionUID = 5754887415071510653L;

    /**
     * 表ID
     */
    private String tableId;

    /**
     * 表数据
     */
    private List<JSONObject> dataList;
}
