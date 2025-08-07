package com.wenge.model.vo;

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
public class StrutsResultVo {
    private String content;
    private String tableName;
    private String createTime;
    private String businessId;
    private Integer type;
}