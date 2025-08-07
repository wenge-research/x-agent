package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: caohaifeng
 * @date: 2024/8/14 14:45
 * @Description: 大模型分页查询参数实体
 * @Version:1.0
 **/
@Data
public class LlmInfoPageParam extends WgPageInfo {
    /**
     * 模型编码
     */
    @ApiModelProperty(name = "modelCode", value = "模型编码", dataType = "String")
    private String modelCode;

    /**
     * 模型名称
     */
    @ApiModelProperty(name = "modelName", value = "模型名称", dataType = "String")
    private String modelName;

    /**
     * 大模型接口地址
     */
    @ApiModelProperty(name = "uri", value = "大模型接口地址", dataType = "String")
    private String uri;

    /**
     * 状态[启用/停用]
     */
    @ApiModelProperty(name = "status", value = "状态[启用/停用]", dataType = "String")
    private String status;

    /**
     * 大模型创建人归属类型
     */
    @ApiModelProperty(name = "ownerType", value = "大模型创建人归属类型", dataType = "String")
    private String ownerType;

    /**
     * 模型主键id
     */
    @ApiModelProperty(name = "id", value = "主键", dataType = "Integer")
    private Integer id;
}
