package com.wenge.oauth.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PermissionGrantParam extends WGParam {

    private static final long serialVersionUID = 3892462041087476936L;

    private String roleId;
    private List<String> menuId;
}
