package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.entity.AuditNoPassReasons;

import java.util.List;

public interface AuditNoPassReasonsService extends IService<AuditNoPassReasons> {


    List<AuditNoPassReasons> getAuditNoPassReasons(Integer pid);
}