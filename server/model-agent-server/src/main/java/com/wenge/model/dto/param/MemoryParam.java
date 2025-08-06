package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;

@Data
public class MemoryParam extends WgPageInfo {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 工作流应用id
     */
    private String applicationId;

    /**
     * 主题
     */
    private String subject;

    /**
     * 内容
     */
    private String content;

    /**
     * 对话id 该条记忆是从哪个对话中总结的
     */
    private String dialogueId;

    /**
     * 记忆创建时间
     */
    private String createTime;
}
