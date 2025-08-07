package com.wenge.model.dto.param;

import lombok.Data;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/15 15:57
 */

@Data
public class MatterFiledInfoParam {
    private Long id;
    private String matterId;

    private String filedName;

    private String filedCode;

    /**
     * 场景id
     */
    private String sceneId;
}