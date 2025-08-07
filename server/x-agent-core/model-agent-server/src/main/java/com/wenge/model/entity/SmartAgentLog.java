package com.wenge.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.Score;
import org.dromara.easyes.annotation.rely.IdType;

import java.io.Serializable;

@Data
@Builder
@IndexName
@AllArgsConstructor
@NoArgsConstructor
public class SmartAgentLog implements Serializable {

    private static final long serialVersionUID = 8901334677509447839L;
    // 知识库ID
    private String knoId;
    // 操作类型
    private String opType;
    // 操作人ID
    private Long opUserId;
    // 操作人名称
    private String opUserName;
    // 操作内容
    private String opContent;
    // 操作时间
    private String opTime;

    @IndexId(type = IdType.CUSTOMIZE)
    private String id;
    @Score
    private Float score;
}
