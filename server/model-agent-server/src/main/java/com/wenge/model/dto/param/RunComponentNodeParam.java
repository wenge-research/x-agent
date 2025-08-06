package com.wenge.model.dto.param;

import cn.hutool.json.JSONObject;
import com.wenge.model.entity.LlmInfo;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class RunComponentNodeParam {
    /**
     * 组件id
     */
    private String componentId;

    /**
     * 对话id
     */
    private String dialogueId;

    /**
     * 临时存放token
     */
    private String token;

    /**
     * 会话id
     */
    private String conversationId;

    /**
     * 输入参数
     */
    private JSONObject inputs;

    /**
     * 文件
     */
    private List<MultipartFile> files;

    /**
     * 模型
     */
    private String clientId;

    /**
     * 回答
     */
    private String answer;

    /**
     * 问题
     */
    private String question;

    /**
     * 客户端类型
     */
    private String clientType;

    /**
     * 大模型信息，如果有给定参数，那么就会以此为准
     */
    private List<LlmInfo> llmInfoList;
}
