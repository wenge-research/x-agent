package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AppointmentConsultationParam extends WgPageInfo {

    private Long id;

    private Integer status;

    private String phone;

    private String companyName;

    private Integer companyType;

    private String handler;

    private String endTime;

    private String startTime;
}
