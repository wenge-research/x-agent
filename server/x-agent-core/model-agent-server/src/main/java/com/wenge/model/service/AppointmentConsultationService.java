package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.AppointmentConsultationParam;
import com.wenge.model.entity.AppointmentConsultation;
import com.wg.appframe.core.bean.Result;

import javax.servlet.http.HttpServletResponse;

public interface AppointmentConsultationService extends IService<AppointmentConsultation> {
    Result add(AppointmentConsultation appointmentConsultation);

    Result setStatus(AppointmentConsultationParam param);

    Result<Page<AppointmentConsultation>> getAppointmentConsultations(AppointmentConsultationParam param);

    void export(AppointmentConsultationParam param, HttpServletResponse response);
}
