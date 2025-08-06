package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.ApplicationSuggestionFeedbackPageParam;
import com.wenge.model.dto.param.AppointmentConsultationParam;
import com.wenge.model.entity.AppointmentConsultation;
import com.wenge.model.service.AppointmentConsultationService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/appointmentConsultation")
@Slf4j
@Api("预约咨询")
public class AppointmentConsultationController {

    @Autowired
    private AppointmentConsultationService appointmentConsultationService;

    /**
     * 新增预约&咨询
     */
    @ApiOperation(value = "新增预约&咨询",tags = "新增预约&咨询", notes = "新增预约&咨询", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/add")
    @OperaLogs
    public Result add(@RequestBody AppointmentConsultation appointmentConsultation) {
        return appointmentConsultationService.add(appointmentConsultation);
    }

    /**
     * 修改预约&咨询的完成状态
     */
    @ApiOperation(value = "修改预约&咨询状态",tags = "修改预约&咨询状态", notes = "修改预约&咨询状态", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/setStatus")
    @OperaLogs
    public Result setStatus(@RequestBody AppointmentConsultationParam param) {
        return appointmentConsultationService.setStatus(param);
    }

    /**
     * 查询预约咨询列表
     */
    @ApiOperation(value = "查询预约&咨询状态",tags = "查询预约&咨询状态", notes = "查询预约&咨询状态", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getAppointmentConsultations")
    @OperaLogs
    public Result<Page<AppointmentConsultation>> getAppointmentConsultations(@RequestBody AppointmentConsultationParam param) {
        return appointmentConsultationService.getAppointmentConsultations(param);
    }

    /**
     * 导出预约&咨询
     * @param param
     * @param response
     */
    @ApiOperation(value = "导出预约&咨询")
    @PostMapping("/export")
    @OperaLogs
    public void export(@RequestBody AppointmentConsultationParam param, HttpServletResponse response) {
        appointmentConsultationService.export(param, response);
    }
}
