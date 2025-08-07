package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class KnowledgeInfoPageParam extends WgPageInfo {

    private static final long serialVersionUID = -5270879814240389813L;

    private String knowledgeName;
    private String applicationId;

    private String knowledgeId;

    private String createUser;

    private String startTime;

    private String endTime;

    private String flType;

    private String ownerType;

    private Long id;

    private String status;
}
