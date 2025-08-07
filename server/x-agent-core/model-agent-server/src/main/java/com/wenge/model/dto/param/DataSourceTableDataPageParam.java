package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataSourceTableDataPageParam extends WgPageInfo {

    private static final long serialVersionUID = -2636192558690322065L;

    private String dataSourceId;
    private String bussiness;

    private String tableName;
}
