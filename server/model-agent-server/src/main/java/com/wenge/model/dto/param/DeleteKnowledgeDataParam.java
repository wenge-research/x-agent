package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeleteKnowledgeDataParam extends WGParam {

    private static final long serialVersionUID = 2680828954013229675L;

    private List<String> id;
    private List<String> knowledgeId;
}
