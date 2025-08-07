package com.wenge.model.dto.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/15 15:57
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMatterGuideDataParam {
    /**
     * 事项id
     */
    private String matterId;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 信息id
     */
    private String infoId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 流水号id
     */
    private String traceId;

    /**
     * 数据项
     */
    private List<AddMatterGuideDataItem> items;
}