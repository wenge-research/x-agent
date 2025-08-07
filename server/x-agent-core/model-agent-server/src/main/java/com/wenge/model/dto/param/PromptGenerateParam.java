package com.wenge.model.dto.param;

import com.mybatisflex.annotation.Column;
import com.wg.appframe.core.dto.params.WGParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PromptGenerateParam extends WGParam {

    private static final long serialVersionUID = -235867099807291503L;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 主题
     */
    private String topic;

    /**
     * 描述
     */
    private String description;

    /**
     * 策略,dbImageStrategy,yayiImageStrategy,minmaxImageStrategy
     */
    private String strategy;

    /**
     * 图片宽度
     */
    private Integer width;

    /**
     * 图片高度
     */
    private Integer height;

    /**
     * 图片自由度，取值范围[1-10]（豆包模型专用）
     */
    private Float guidanceScale;

    /**
     * 图片数量（minmax模型专用）
     */
    private Integer count;

    /**
     * 是否保存到 minio，默认是
     */
    private String saveFlag;

    /**
     * 应用ID
     */
    private String applicationId;

    /**
     * 模型ID
     */
    private String modelId;

    /**
     * 参考图（注：minmax模型（image-01版本）专用）
     * 注：可通过公网访问的 URL；将以字符串形式存储在数组中（暂时数组长度仅支持为 1，即单图参考）。图片文件内存：小于10M。图片格式：jpg、jpeg、png。
     */
    private List<String> imageUrls;

    /**
     * 比例
     */
    private String ratio;

    /**
     * 控制是否开启prompt自动优化（注：minmax模型专用）
     */
    private Boolean promptOptimizer;

    /**
     * 画风风格类型设置，可选值: 漫画、元气、中世纪、水彩（注： minmax模型（image-01-live版本）专用）
     */
    private String styleType;
    /**
     * 画风控制权重。支持设置范围（0,1]，默认值为0.8（注：minmax模型（image-01-live版本）专用）
     */
    private Float styleWeight;

    /**
     * 问题数量
     */
    private Integer questionNum;

    /**
     * 源图片地址
     */
    private String imageUrl;

}
