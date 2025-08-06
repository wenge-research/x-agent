package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@ApiModel
@Data(staticConstructor = "create")
@Table(value = "application_business_relation")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ApplicationBusinessRelation {

    @Column("id")
    private Long id;

    @Column("type")
    @ApiModelProperty(name = "type", value = "业务类型", dataType = "Integer")
    private Integer type;

    @Column("type_name")
    @ApiModelProperty(name = "typeName", value = "业务类型名称", dataType = "String")
    private String typeName;

    @Column("application_id")
    @ApiModelProperty(name = "applicationId", value = "应用id", dataType = "String")
    private String applicationId;

    /**
     * 更新时间
     */
    @Column("update_time")
    @ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
    private String updateTime;

    /**
     * 创建时间
     */
    @Column("create_time")
    @ApiModelProperty(name = "createTime", value = "更新时间", dataType = "String")
    private String createTime;

}
