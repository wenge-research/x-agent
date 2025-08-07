package com.wg.appframe.yayi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 调用雅意问答接口的配置
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class YayiDialogueFilterConfig extends YayiConfigSupper {

    private static final long serialVersionUID = -6071095343924945576L;

    private String uri;
    private String preprocessType;
    private Float cosineThresholdValue;
    private Float distanceThresholdValue;
    private Integer n;

}
