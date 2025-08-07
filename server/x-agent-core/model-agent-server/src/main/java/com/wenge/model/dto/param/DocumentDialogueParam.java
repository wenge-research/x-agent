package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DocumentDialogueParam extends WGParam {

    private static final long serialVersionUID = 8423288429253667684L;

    /**
     * 问题内容
     */
    private String question;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 自定义prompt
     */
    private String customizePrompt;

    /**
     * AppId
     */
    private String appId;

    /**
     * 模块，bidding-招标，bid-投标，gx_matter-关芯智巡...(后续有更多待定)
     */
    private String module;

    /**
     * 对话id
     */
    private String clientId;

}
