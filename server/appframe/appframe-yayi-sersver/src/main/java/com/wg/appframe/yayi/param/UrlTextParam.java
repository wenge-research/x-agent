package com.wg.appframe.yayi.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class UrlTextParam extends YayiParam implements Serializable  {

    private static final long serialVersionUID = -717140750174309839L;

    private String url;
    private String mode;
    private Boolean ignore_links;
    private Boolean ignore_images;

}
