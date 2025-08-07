package com.wenge.model.dto.param;

import com.mybatisflex.annotation.Column;
import com.wg.appframe.core.bean.WgPageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TableDirectoryDataPageParam extends WgPageInfo {

    private static final long serialVersionUID = -2636192558690322065L;

    @ApiModelProperty(name = "jdbcUrl", value = "JDBC路径", dataType = "String")
    private String jdbcUrl;

    @ApiModelProperty(name = "jdbcName", value = "JDBC用户", dataType = "String")
    private String jdbcName;

    @ApiModelProperty(name = "jdbcPassword", value = "JDBC密码", dataType = "String")
    private String jdbcPassword;

    @ApiModelProperty(name = "tableName", value = "表名", dataType = "String")
    private String tableName;
}
