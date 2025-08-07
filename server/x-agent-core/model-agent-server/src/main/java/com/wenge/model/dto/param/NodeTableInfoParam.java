package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NodeTableInfoParam extends WgPageInfo {

    private static final long serialVersionUID = -5485372313276498338L;

    private String tableName;
}
