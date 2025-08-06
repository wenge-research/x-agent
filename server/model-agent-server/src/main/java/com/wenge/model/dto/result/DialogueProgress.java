package com.wenge.model.dto.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 对话时实时推送进度
 */
@Data
public class DialogueProgress implements Serializable {

    private static final long serialVersionUID = 8750582127147142162L;

    /**
     * 当前步骤
     */
    private String stepIndex;

    /**
     * 步骤名称
     */
    private String progress;

    /**
     * 步骤结果集
     */
    private List<Object> resultList;

}
