package com.wenge.model.dto.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Description: 文件实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 18:06:35
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrutsDelParam{
    private List<StrutsDelItem> strutsDelItems;
}