package com.wenge.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Column;
import com.wenge.model.constants.MybatisFileConstant;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/10 15:03
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StructureParserVo {
    private Long id;// 主键id
    private String businessId;// 业务id
    private String type;// 类型 excel还是数据源
    private Integer parserFlag;// 是否解析过 1 解析 2未解析
    private Integer enableFlag;// 状态0 关闭 1启用
    private Integer deleteFlag;// 是否删除了 0未删除 1删除
    private String desc;// 描述
    private String description;// 描述

    //问数文件同步大模型分析处理 0-未同步 2-同步中 3-同步完成 4-同步异常（重新同步） 默认-0
    private String synchStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;// 创建时间

    private String validDate;// 有效时间


    private String updateUser;//更新人
    private String createUser;//更新人

    private String knowledgeId;


}