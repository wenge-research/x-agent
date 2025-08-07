package com.wenge.model.dto.param;

import com.wenge.model.entity.ComponentNodeTableField;
import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class NodeTableFieldAddParam extends WGParam {

    private static final long serialVersionUID = -4430673744408492870L;

    /**
     * 表业务id
     */
    private String tableId;

    /**
     * 字段信息
     */
    private List<ComponentNodeTableField> fieldList;
}
