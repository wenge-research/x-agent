package com.wg.appframe.yayi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 调用雅意问答接口的配置
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class YayiEmbeddingConfig extends YayiConfigSupper {

    private static final long serialVersionUID = 7634392659556860258L;

    private String uri;
    private String function;
    private String model;
    private String dataType;

}
