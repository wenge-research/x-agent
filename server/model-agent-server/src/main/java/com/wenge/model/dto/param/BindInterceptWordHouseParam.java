package com.wenge.model.dto.param;

import lombok.Data;

import java.util.List;

/**
 * 批量绑定拦截词库参数
 */
@Data
public class BindInterceptWordHouseParam {
    /**
     * 绑定的应用id
     */
    private String applicationId;

    /**
     * 绑定的拦截词库id 没有的会删除
     */
    private List<Long> ids;

}
