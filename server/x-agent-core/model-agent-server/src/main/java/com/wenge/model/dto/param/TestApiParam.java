package com.wenge.model.dto.param;

import cn.hutool.json.JSONObject;
import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TestApiParam extends WGParam {

    private static final long serialVersionUID = 4177462067149249797L;

    /**
     *   请求地址
     */
    private String url;

    /**
     *  请求方式：GET，POST
     */
    private String method;

    /**
     *  请求体
     */
    private JSONObject body;

    /**
     *  路径参数,格式${name}
     */
    private JSONObject path;


    /**
     *  请求头
     */
    private JSONObject header;

    /**
     *  请求参数
     */
    private JSONObject query;

    /**
     *  表单参数
     */
    private JSONObject formData;

}
