package com.wenge.model.utils;

import com.wenge.model.entity.SmartAgentLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class LogUtils {

    public static SmartAgentLog buildSmartAgentLog(String knoId,
                                   String opType,
                                   Long opUserId,
                                   String opUserName,
                                   String opContent,
                                   String opTime){
        return SmartAgentLog.builder()
                .id(UUID.randomUUID().toString())
                .knoId(knoId)
                .opType(opType)
                .opUserId(opUserId)
                .opUserName(opUserName)
                .opContent(opContent)
                .opTime(opTime)
                .build();
    }
}
