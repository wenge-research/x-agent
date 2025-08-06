package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class VoiceComponentInfoPageParam extends WgPageInfo {

    private static final long serialVersionUID = -2636192558690322065L;

    /**
     * 组件名称
     */
    @ApiModelProperty(name = "name", value = "组件名称", dataType = "String")
    private String name;

    /**
     * 组件分类
     */
    @ApiModelProperty(name = "category", value = "组件分类", dataType = "String")
    private String category;

    /**
     * 标签
     */
    @ApiModelProperty(name = "tag", value = "标签", dataType = "String")
    private String tag;

    /**
     * 语音形式(tts：语音识别,stt：语音合成)
     */
    @ApiModelProperty(name = "module", value = "语音形式(tts：语音识别,stt：语音合成)", dataType = "String")
    private String module;

    /**
     * 常用标识 0-非常用 1-常用
     */
    @ApiModelProperty(name = "frequenceUseFlag", value = "常用标识 0-非常用 1-常用", dataType = "Integer")
    private Integer frequenceUseFlag;

    /**
     * 归属类型
     */
    @ApiModelProperty(name = "ownerType", value = "归属类型 official-官方预置 personal-个人", dataType = "String")
    private String ownerType;


}
