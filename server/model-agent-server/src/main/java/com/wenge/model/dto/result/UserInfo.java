package com.wenge.model.dto.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserInfo {

    @ApiModelProperty(name = "name", value = "姓名", dataType = "String")
    private String name;

    @ApiModelProperty(name = "phone", value = "电话", dataType = "String")
    private String phone;

    @ApiModelProperty(name = "education", value = "学历", dataType = "String")
    private String education;

}
