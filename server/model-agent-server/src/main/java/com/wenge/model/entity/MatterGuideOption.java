package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryWrapper;
import com.wg.appframe.mybatisflex.core.FlexModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/15 14:23
 */
@ApiModel
@Data(staticConstructor = "create")
@Table(value = "matter_guide_option")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MatterGuideOption extends FlexModel<MatterGuideOption> {
    @Column(ignore = true)
    private QueryWrapper currentWrapper;

    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(value = "主键，自增id，没有业务作用")
    private Long id;

    @Column("filed_id")
    @ApiModelProperty(value = "字段ID")
    private String filedId;


    @Column("value")
    @ApiModelProperty(value = "值")
    private String value;

    @Column("lable")
    @ApiModelProperty(value = "lable")
    private String lable;

    @Column("deleted_flag")
    @ApiModelProperty(name = "deletedFlag", value = "deletedFlag", dataType = "Integer")
    private Integer deletedFlag;

}