package com.wenge.model.utils.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TableInfoExport {
    //系统名称
    @ExcelProperty("系统名称")
    private String systemName = "“智川”生成式AI应用创新引擎";
    //数据库名称
    @ExcelProperty("数据库名称")
    private String databaseName = "smart_customer_agent_demo";

    //表英文名称
    @ExcelProperty("表英文名称")
    private String tableEnglishName;

    //表中文名称
    @ExcelProperty("表中文名称")
    private String tableChineseName;

    //表属性
    @ExcelProperty("表属性")
    private String tableComment = "业务表";

    //更新频率
    @ExcelProperty("更新频率")
    private String updateFrequency;


    //当前数据表条数
    @ExcelProperty("当前数据表条数")
    private String currentDataTableNumber;
    //增量数据条数
    @ExcelProperty("增量数据条数")
    private String incrementalDataNumber;

    //字段序号
    @ExcelProperty("字段序号")
    private String fieldNumber;
    //字段英文名
    @ExcelProperty("字段英文名")
    private String fieldEnglishName;
    //字段中文名
    @ExcelProperty("字段中文名")
    private String fieldChineseName;
    //字段类型
    @ExcelProperty("字段类型")
    private String fieldType;
    //字段长度
    @ExcelProperty("字段长度")
    private String fieldLength;

    //是否为增量字段
    @ExcelProperty("是否为增量字段")
    private String isIncrementalField = "否";

    //是否主键
    @ExcelProperty("是否主键")
    private String isPrimaryKey;
    //是否为空
    @ExcelProperty("是否为空")
    private String isNull;

    //注释（参考标准号）
    @ExcelProperty("注释（参考标准号）")
    private String referenceStandardNumber;

}