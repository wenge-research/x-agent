package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NodeTableDataListParam extends WgPageInfo {

    private static final long serialVersionUID = -6716529142964776985L;

    /**
     * 表业务 id
     */
    private String tableId;
}
