package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询场景管理列表参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SceneManagementPageParam extends WgPageInfo {

    private static final long serialVersionUID = 3836621791764989158L;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 应用ID
     */
    private String applicationId;
}
