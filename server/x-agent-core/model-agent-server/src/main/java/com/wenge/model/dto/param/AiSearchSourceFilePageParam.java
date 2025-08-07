package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AiSearchSourceFilePageParam extends WgPageInfo {

    private static final long serialVersionUID = -7871101732390296038L;


    /**
     * 文件名
     */
    private String fileName;

    /**
     * 开始创建时间
     */
    private String createStartTime;

    /**
     * 结束创建时间
     */
    private String createEndTime;

    /**
     * 收录开始时间
     */
    private String recordStartTime;

    /**
     * 收录结束时间
     */
    private String recordEndTime;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件类型： pdf docx txt pptx xlsx png
     */
    private List<String> fileTypes;

    /**
     * 图片颜色：图片颜色类型 0-红色 1-白色 2-紫色 3-绿色
     */
    private Integer picColorType;

    /**
     * 文档所属分类: civil_administration  police  other  city_supervisor  uncategorized  city_supervisor
     */
    private String type;

}
