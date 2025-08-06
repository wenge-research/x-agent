package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/12 17:44
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrutsResultPageParam extends WgPageInfo {
    private String businessId;
    private String tableName;
}