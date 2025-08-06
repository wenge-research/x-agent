package com.wenge.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("APP智能问答根据关键词推荐列表响应参数")
public class RecommendedListByKeywordVo {
    private String code;
    private String msg;

    private List<Detail> data;

    @Data
    public static class Detail {
        @ApiModelProperty("主键")
        private String id;
        @ApiModelProperty("封面图附件id")
        private String surfaceDrawingFileId;
        @ApiModelProperty("封面图")
        private String surfaceDrawingFile;
        @ApiModelProperty("职位名称")
        private String positionName;
        @ApiModelProperty("工作时间描述")
        private String workingHoursDescription;
        @ApiModelProperty("薪资待遇")
        private String salaryAndTreatment;
        @ApiModelProperty("薪资结算周期：1、日结；2、周结；3、月结；")
        private String payrollCycle;
        @ApiModelProperty("薪资")
        private String pay;
        @ApiModelProperty("是否待遇面议")
        private String isTreatmentNegotiable;
        @ApiModelProperty("详情描述")
        private String detailDescription;
        @ApiModelProperty("跳转链接")
        private String url;
    }

}
