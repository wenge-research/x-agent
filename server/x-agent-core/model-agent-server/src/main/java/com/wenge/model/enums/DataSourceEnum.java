package com.wenge.model.enums;



/**
 * 数据源类型枚举
 */
public enum DataSourceEnum {
    MYSQL(1, "MYSQL",  "mysql数据源"),
    DM(2, "DM",  "达梦数据源"),
    ;

    // 数据源类型编码
    private Integer dsTypeCode;
    // 数据源名称
    private String dsName;
    // 描述
    private String desc;

    DataSourceEnum(Integer dsTypeCode, String dsName, String desc) {
        this.dsTypeCode = dsTypeCode;
        this.dsName = dsName;
        this.desc = desc;
    }

    public Integer getDsTypeCode() {
        return dsTypeCode;
    }

    public void setDsTypeCode(Integer dsTypeCode) {
        this.dsTypeCode = dsTypeCode;
    }

    public String getDsName() {
        return dsName;
    }

    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
