package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PromptConfigListParam extends WgPageInfo {

    private static final long serialVersionUID = 4642585499118957419L;

    /**
     * 提示词标题
     */
    private String promptTitle;

    /**
     * 类型，system:系统提示词,user:用户提示词,yayi:机器人提示词
     */
    private String type;

    /**
     * 提示词内容
     */
    private String content;

    /**
     * 0-有效  1-无效
     */
    private Integer status;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 提示词归属类型
     */
    private String ownerType;

    /**
     * 提示词的主键id
     */
    private Integer id;
}
