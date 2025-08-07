package com.wg.appframe.yayi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SecurityConfig extends YayiConfigSupper {

    private static final long serialVersionUID = -2579091525285612905L;

    private String uri;
    private String content;
    private String mode;
    private List<String> securityLevel;
}
