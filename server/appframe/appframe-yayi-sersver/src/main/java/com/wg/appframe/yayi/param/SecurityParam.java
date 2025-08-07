package com.wg.appframe.yayi.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class SecurityParam extends YayiParam implements Serializable  {

    private static final long serialVersionUID = 8455476961321687081L;

    private String content;
    private String mode;

}
