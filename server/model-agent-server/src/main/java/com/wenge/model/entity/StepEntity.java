package com.wenge.model.entity;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;

@Data
public class StepEntity implements Serializable {

    private static final long serialVersionUID = 3414250298204888356L;

    /**
     * 对话id
     */
    private String dialogueId;

    /**
     * 会话id
     */
    private String conversationId;

    /**
     * 步骤
     */
    private String step;

    /**
     * 提示
     */
    private Object prompt;

    /**
     * 结果
     */
    private Object result;

    /**
     * 只是用来传递参数，不要用于业务
     */
    private JSONObject param;

    private Integer tokenTotal = 0;

    /**
     * 耗时,毫秒
     */
    private Long costTime;

    /**
     * 大模型首次返回的时间
     */
    private Long firstLlmTime;

    /**
     * 创建的时间
     */
    private String createTime;


    /**
     * 结束的时间
     */
    private String endTime;

}
