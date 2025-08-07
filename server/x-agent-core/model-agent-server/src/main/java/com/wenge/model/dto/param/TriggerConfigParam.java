package com.wenge.model.dto.param;

import com.mybatisflex.annotation.Column;
import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TriggerConfigParam extends WgPageInfo {

    private static final long serialVersionUID = 3037384264619002498L;

    private String applicationId;

    private String componentId;

    @Column("status")
    private Integer status;

}
