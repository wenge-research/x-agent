package com.wenge.model.dto.param;

import com.wenge.model.entity.MatterGuideFiled;
import com.wg.appframe.core.bean.WgPageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DenseVectorPageParam extends WgPageInfo {

    private String keyword;

}
