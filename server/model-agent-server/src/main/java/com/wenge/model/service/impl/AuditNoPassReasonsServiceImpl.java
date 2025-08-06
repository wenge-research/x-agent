package com.wenge.model.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.entity.AuditNoPassReasons;
import com.wenge.model.mapper.AuditNoPassReasonsMapper;
import com.wenge.model.service.AuditNoPassReasonsService;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wenge.model.entity.table.AuditNoPassReasonsTableDef.AUDIT_NO_PASS_REASONS;

@Service
@Slf4j
public class AuditNoPassReasonsServiceImpl extends ServiceImpl<AuditNoPassReasonsMapper, AuditNoPassReasons> implements AuditNoPassReasonsService {

    @Override
    public List<AuditNoPassReasons> getAuditNoPassReasons(Integer pid) {
        if(pid == null){
            pid = 0;
        }
        Wrappers wrappers = Wrappers.init()
                .where(AUDIT_NO_PASS_REASONS.STATUS.eq(0))
                .and(AUDIT_NO_PASS_REASONS.PID.eq(pid))
                .orderBy(AUDIT_NO_PASS_REASONS.CREATE_TIME.asc());
        return list(wrappers);
    }
}