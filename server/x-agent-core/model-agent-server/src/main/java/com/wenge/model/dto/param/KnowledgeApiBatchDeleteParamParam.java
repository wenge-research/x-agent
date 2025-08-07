package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class KnowledgeApiBatchDeleteParamParam extends WgPageInfo {

    private static final long serialVersionUID = 3037384264619002498L;

    private List<Long> ids;

}
