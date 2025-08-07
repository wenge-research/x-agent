package com.wg.appframe.yayi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AiImageConfig extends YayiConfigSupper {
    private static final long serialVersionUID = 3208101305768302133L;

    private String uri;
}
