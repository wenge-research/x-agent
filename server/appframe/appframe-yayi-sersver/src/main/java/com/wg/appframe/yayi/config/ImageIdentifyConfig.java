package com.wg.appframe.yayi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImageIdentifyConfig extends YayiConfigSupper {

    private static final long serialVersionUID = -6236593295565251511L;

    private String uri;
    private Integer getOcr;
    private Integer getCaption;
}
