package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExcelParserTablePageParam extends WgPageInfo {

    private static final long serialVersionUID = -2636192558690322065L;

    @ApiModelProperty(name = "excelId", value = "业务id", dataType = "String")
    private String excelId;

    @ApiModelProperty(name = "keyword", value = "关键字", dataType = "String")
    private String keyword;
}
