package com.wenge.oauth.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GrantDataGetParam extends WGParam {

    private static final long serialVersionUID = -153148851168091052L;

    private String dataId;
    private String dataType;
    private String targetType;
}
