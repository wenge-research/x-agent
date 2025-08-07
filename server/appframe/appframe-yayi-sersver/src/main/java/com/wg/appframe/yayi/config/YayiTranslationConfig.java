package com.wg.appframe.yayi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YayiTranslationConfig extends YayiConfigSupper {

    private static final long serialVersionUID = 5100290339043451381L;

    private String uri;
    private String srcLang;
    private String tgtLang;
}
