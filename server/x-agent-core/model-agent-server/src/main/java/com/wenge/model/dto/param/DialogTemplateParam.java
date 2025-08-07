package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DialogTemplateParam extends WgPageInfo {

    private static final long serialVersionUID = 6465609064207321840L;

    /**
     * 聊天模板形式[PC/H5]
     */
    private String form;

    /**
     * 聊天模板类型 smart_qa[智能问答], smart_report[智能报告], smart_search[智能搜索], smart_translate[智能翻译], smart_review[智能审查]
     */
    private String intelligenceType;

    /**
     * 聊天模板名称
     */
    private String dialogTemplateName;

    @ApiModelProperty(name = "status", value = "是否启用: 0 禁用, 1 启用", dataType = "Integer")
    private Integer status;

}
