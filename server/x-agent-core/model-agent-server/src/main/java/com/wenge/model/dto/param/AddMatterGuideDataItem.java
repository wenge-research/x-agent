package com.wenge.model.dto.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/15 15:57
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMatterGuideDataItem {

    /**
     * 字段id
     */
    private String filedId;

    /**
     * 字段值
     */
    private Object dataValue;


}