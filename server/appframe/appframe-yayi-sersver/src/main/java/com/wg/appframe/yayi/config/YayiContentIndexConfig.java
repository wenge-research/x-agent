package com.wg.appframe.yayi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YayiContentIndexConfig extends YayiConfigSupper{

    private static final long serialVersionUID = 4819263038937989454L;

    private String uri;
    private String mode;
}
