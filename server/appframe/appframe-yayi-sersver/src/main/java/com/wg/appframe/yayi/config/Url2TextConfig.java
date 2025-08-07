package com.wg.appframe.yayi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Url2TextConfig extends YayiConfigSupper{

    private static final long serialVersionUID = -2760930146314568472L;
    
    private String uri;
    private String url;
    private String mode;
    private Boolean ignoreLinks;
    private Boolean ignoreImages;
}
