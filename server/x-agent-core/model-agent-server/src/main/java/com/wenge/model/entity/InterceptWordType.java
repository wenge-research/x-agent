package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel
@Data
@Table(value = "intercept_word_type")
@EqualsAndHashCode(callSuper = false)
public class InterceptWordType {

    @Column("id")
    @ApiModelProperty(name = "id", value = "主键自增", dataType = "Long")
    private Long id;

    @Column("type_name")
    @ApiModelProperty(name = "typeName", value = "类型名称", dataType = "String")
    private String type_name;

    @Column("status")
    @ApiModelProperty(name = "status", value = "状态：0-有效；1-无效", dataType = "String")
    private String status;
}
