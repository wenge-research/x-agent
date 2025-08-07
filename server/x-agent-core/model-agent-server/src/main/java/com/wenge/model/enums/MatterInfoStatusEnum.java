package com.wenge.model.enums;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/18 10:05
 */
public enum MatterInfoStatusEnum {

    APPROVE(0,"未审批"),
    UN_APPROVE(1,"已审批");


    private Integer status;
    private String dec;

    public Integer getStatus() {
        return status;
    }

    public String getDec() {
        return dec;
    }

    MatterInfoStatusEnum(Integer status, String dec) {
        this.status = status;
        this.dec = dec;
    }

    MatterInfoStatusEnum() {
    }
}
