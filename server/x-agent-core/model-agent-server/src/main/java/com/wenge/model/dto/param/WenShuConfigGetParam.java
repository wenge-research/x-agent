package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class WenShuConfigGetParam extends WGParam {

    private static final long serialVersionUID = -2267357469736466846L;

    /**
     * 知识库id
     */
    private List<String> knowledgeIdList;
}
