package com.wenge.model.dto.param;

import cn.hutool.json.JSONObject;
import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CollectDataEditParam extends WGParam {

    private static final long serialVersionUID = 626405383642689768L;

    private String configId;

    /**
     * 删除的数据
     */
    private List<Integer> deleteIdList;

    /**
     * 新增的数据
     */
    private List<JSONObject> addDataList;
}
