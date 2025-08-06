package com.wenge.model.dto.result;

import com.alibaba.fastjson2.JSONObject;
import com.wg.appframe.core.dto.results.WGResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 关芯智巡结果
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProviderGuanxinResult extends WGResult {

    private static final long serialVersionUID = -538659473505466776L;

    /**
     * 请求id，原路返回
     */
    private String id;

    /**
     * 数据id，原路返回
     */
    private String dataId;

    /**
     * 类型，1-事项，2-解析图片
     */
    private String type;

    /**
     * 描述图片，是一个json数组，每个元素是一个图片的url
     */
    @ApiModelProperty(name = "parseImageList", value = "描述图片，是一个json数组，每个元素是一个图片的url", dataType = "Array")
    private List<JSONObject> parseImageList;

    /**
     * 检查结果
     */
    private List<GxResult> resultList;

    @Data
    public static class GxResult implements Serializable {

        private static final long serialVersionUID = -4544778821090311236L;

        /**
         * 事项名称
         */
        private String matterName;

        /**
         * 指标名称
         */
        private String itemName;

        /**
         * 检查结果
         */
        private String check;
    }
}
