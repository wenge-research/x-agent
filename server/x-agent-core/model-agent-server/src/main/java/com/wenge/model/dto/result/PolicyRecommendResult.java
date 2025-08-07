package com.wenge.model.dto.result;

import com.wg.appframe.core.dto.results.WGResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PolicyRecommendResult extends WGResult {

    private static final long serialVersionUID = -8645304901344036780L;


    /**
     * 推荐问题列表
     */
    private List<String> filedList;
}
