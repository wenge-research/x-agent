package com.wenge.oauth.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MenuHiddenParam extends WGParam {

    private static final long serialVersionUID = -3607863987750989624L;

    private String menuId;
    private Integer isHidden;
}
