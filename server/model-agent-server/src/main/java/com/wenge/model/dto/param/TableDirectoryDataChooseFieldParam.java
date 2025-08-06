package com.wenge.model.dto.param;

import com.wenge.model.vo.TableInfoVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TableDirectoryDataChooseFieldParam{

    private String businessId;

    private String tableName;

    private List<TableInfoVo> tableInfoVos;
}
