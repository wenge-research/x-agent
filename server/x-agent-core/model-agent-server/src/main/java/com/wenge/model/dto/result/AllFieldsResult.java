package com.wenge.model.dto.result;

import com.wenge.model.entity.MatterGuideFiled;
import com.wg.appframe.core.dto.results.WGResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AllFieldsResult extends WGResult {

    private static final long serialVersionUID = -8645304901344036780L;

    /**
     * 分组
     */
    private String group;

    /**
     * 字段列表
     */
    private List<MatterGuideFiled> filedList;
}
