package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@ApiModel
@Data
@Table(value = "intercept_word_handling_method")
@EqualsAndHashCode(callSuper = false)
@Setter
@Getter
public class InterceptWordHandlingMethod {

    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(value = "主键，自增id，没有业务作用")
    private Long id;

    @Column("method_name")
    @ApiModelProperty(value = "处理方式")
    private String method_name;

    @Column("status")
    @ApiModelProperty(value = "状态：0-有效；1-无效")
    private String status;

}