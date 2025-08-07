package com.wg.appframe.wos.dto.result;

import cn.hutool.core.util.IdUtil;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class WosResult<T> implements Serializable {

    private static final long serialVersionUID = -7026983724236413307L;

    private final String code;
    private final String msg;
    private T data;
    private final String time;
    private final String traceId;

    protected WosResult(String code, String msg) {
        this.traceId = IdUtil.simpleUUID();
        this.code = code;
        this.msg = msg;
        this.time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    public WosResult(String code, String msg, T data) {
        this(code, msg);
        this.data = data;
    }

    public static <T> WosResult<T> success(T data) {
        return new WosResult<>("000000", "成功", data);
    }
}
