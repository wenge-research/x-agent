package com.wenge.model.dto.result;

import lombok.Data;

@Data
public class AddCatalogResult {

    private Long appInfoId;
    /**
     * 返回的list
     */
    private String[] data;
    /**
     * APP分页是否是最后一页
     */
    private Boolean end;
    /**
     * 错误码：0=正常
     */
    private String errorCode;
    /**
     * 请求提示信息
     */
    private String message;
    private String requestId;
    /**
     * 请求结果：成功/失败
     */
    private Boolean success;
    /**
     * 分页返回的数据总数
     */
    private Long total;
}
