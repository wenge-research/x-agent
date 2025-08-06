package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PresetQuestionPageParam extends WgPageInfo {

    private static final long serialVersionUID = -4583495844404370140L;

    private String type;

    private String applicationId;
}
