package com.wenge.model.dto.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/12 15:09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrutsDelItem {
    private String businessId;
    // 1 数据源 2excel
    private Integer type;
}