package com.wenge.model.dto.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 文件实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2025-02-10 14:06:35
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStructDataValidDateParam {
    /**
     * 业务id
     */
    private String businessId;

    /**
     * 有效时间（传空或null表示永久有效）
     */
    private String validDate;
}