package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/15 14:23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "matter_guide_data")
@EqualsAndHashCode(callSuper = false)
public class MatterGuideData {
    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(value = "主键，自增id，没有业务作用")
    private Long id;

    @Column("data_id")
    @ApiModelProperty(value = "数据ID")
    private String dataId;

    @Column("info_id")
    @ApiModelProperty(value = "数据ID")
    private String infoId;

    @Column("matter_id")
    @ApiModelProperty(value = "事项id")
    private String matterId;

    @Column("filed_id")
    @ApiModelProperty(value = "字段ID")
    private String filedId;

    @Column("data_value")
    @ApiModelProperty(value = "数据值")
    private String dataValue;
}