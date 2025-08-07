package com.wenge.model.dto.param;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.wg.appframe.core.bean.WgPageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationInfoVersionParam extends WgPageInfo {
    /**
     * 主键，自增id，没有业务作用
     */
    //@Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(name = "id", value = "主键，自增id，没有业务作用", dataType = "String")
    private String id;
    /**
     * 应用名称
     */
    @ApiModelProperty(name = "applicationName", value = "应用名称", dataType = "String")
    private String applicationName;

    /**
     * 应用id
     */
    @ApiModelProperty(name = "applicationId", value = "应用id，有业务作用", dataType = "String")
    private String applicationId;
    /**
     * 应用id，有业务作用
     */
    //@Column("application_info_id")
    @ApiModelProperty(name = "applicationInfoId", value = "关联application_info表id字段，没有业务作用", dataType = "String")
    private String applicationInfoId;
    /**
     * 应用版本号
     */
    //@Column("app_version_number")
    @ApiModelProperty(name = "appVersionNumber", value = "应用版本号", dataType = "String")
    private String appVersionNumber;
    /**
     * 应用版本说明
     */
    //@Column("app_version_number")
    //@ApiModelProperty(name = "versionRemark", value = "应用版本说明", dataType = "String")
    //private String versionRemark;
    @ApiModelProperty(name = "publishDesc", value = "发布说明", dataType = "String")
    private String publishDesc;
    @ApiModelProperty(name = "backVersionRemark", value = "应用版本回退说明", dataType = "String")
    private String backVersionRemark;
    @ApiModelProperty(name = "startTime", value = "查询开始时间", dataType = "LocalDateTime")
    private String startTime;
    @ApiModelProperty(name = "endTime", value = "查询结束时间", dataType = "LocalDateTime")
    private String endTime;
}
