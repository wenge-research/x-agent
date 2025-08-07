package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class VoiceComponentInfoConfigPageParam extends WgPageInfo {

    private static final long serialVersionUID = -2636192558690322065L;

    /**
     * 组件分类
     */
    private String category;


    /**
     * 组件配置风格常用标识 0-非常用 1-常用
     */
    private Integer frequenceUseFlag;


    /**
     * 归属类型
     */
    @ApiModelProperty(name = "ownerType", value = "归属类型 official-官方预置 personal-个人", dataType = "String")
    private String ownerType;

}
