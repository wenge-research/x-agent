package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationKnowledgeBindedParam extends WGParam {

    private static final long serialVersionUID = -8003370999634617903L;

    private String applicationId;
    private List<String> knowledgeIdList;
}
