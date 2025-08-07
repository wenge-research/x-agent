package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CollectDataListParam extends WgPageInfo {
    
    private static final long serialVersionUID = 2149321927865084685L;
    
    private String configId;
}
