package com.wenge.model.dto.param;

import com.wenge.model.workflow.enums.ComponentTypeEnum;
import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkFlowPageParam extends WgPageInfo {

    private String applicationName;

    private ComponentTypeEnum type;

    private String createUser;

    private Integer id;

    private String ownerType;

    private Integer publishAppStore;

    private Integer status;
}
