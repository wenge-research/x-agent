package com.wenge.model.enums;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/18 10:05
 */
public enum DynamicFormCodeEnum {

    ID_CARD_CHECK("1001","身份证校验"),
    PHONE_CHECK("2001","手机号校验");

    public static String getValue(Integer key){
        DynamicFormCodeEnum[] values = values();
        for (DynamicFormCodeEnum typeEnum : values){
            if(typeEnum.getType().equals(key)){
                return typeEnum.getDec();
            }
        }
        return null;
    }
    public static DynamicFormCodeEnum getInstance(Integer key){
        DynamicFormCodeEnum[] values = values();
        for (DynamicFormCodeEnum typeEnum : values){
            if(typeEnum.getType().equals(key)){
                return typeEnum;
            }
        }
        return null;
    }
    private String type;
    private String dec;

    public String getType() {
        return type;
    }

    public String getDec() {
        return dec;
    }

    DynamicFormCodeEnum(String type, String dec) {
        this.type = type;
        this.dec = dec;
    }

    DynamicFormCodeEnum() {
    }
}
