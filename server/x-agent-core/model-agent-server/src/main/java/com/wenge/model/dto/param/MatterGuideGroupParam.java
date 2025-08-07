package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @BelongsProject: yayi-model-application
 * @BelongsPackage:com.wenge.model.dto.param
 * @Author:caohaifeng
 * @createTime:2024-08-16 :
 * @Description:
 * @Version:1.0
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class MatterGuideGroupParam extends WgPageInfo {

    private static final long serialVersionUID = 1465187901524809176L;

    /**
     * 事项id
     */
    private String matterId;

    /**
     * 场景id
     */
    private String sceneId;

}
