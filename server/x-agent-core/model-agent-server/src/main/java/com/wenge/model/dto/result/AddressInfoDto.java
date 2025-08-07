package com.wenge.model.dto.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddressInfoDto {

    @ApiModelProperty(name = "province", value = "省", dataType = "String")
    private String province;

    @ApiModelProperty(name = "city", value = "市", dataType = "String")
    private String city;

    @ApiModelProperty(name = "area", value = "区", dataType = "String")
    private String area;

    @ApiModelProperty(name = "street", value = "街镇", dataType = "String")
    private String streetTown;

    @ApiModelProperty(name = "quarter", value = "小区", dataType = "String")
    private String quarter;

    @ApiModelProperty(name = "building", value = "楼栋", dataType = "String")
    private String building;

    @ApiModelProperty(name = "unit", value = "单元", dataType = "String")
    private String unit;


}
