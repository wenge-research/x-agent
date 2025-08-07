package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InterceptWordHouseApplicationRelPageParam extends WgPageInfo {

    private static final long serialVersionUID = 1L;

    private String applicationId;

    private String interceptWordHouseId;

}
