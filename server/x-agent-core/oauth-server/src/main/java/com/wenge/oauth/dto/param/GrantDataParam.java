package com.wenge.oauth.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class GrantDataParam extends WGParam {

    private static final long serialVersionUID = -6210691313405210036L;

    private String dataId;
    private String dataType;
    private List<String> targetIdList;
    private String targetType;
    private String remark;
    private Integer copyPermission;
}
