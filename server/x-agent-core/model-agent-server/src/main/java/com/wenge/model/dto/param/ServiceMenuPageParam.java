package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceMenuPageParam extends WgPageInfo {

    private static final long serialVersionUID = 5430276004966913779L;

    private String applicationId;
    private String serviceType;
    private String menuName;
    private String status;
    private String tagName;
}
