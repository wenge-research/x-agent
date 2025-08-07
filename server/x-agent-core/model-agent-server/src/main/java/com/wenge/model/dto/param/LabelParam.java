package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LabelParam  extends WgPageInfo {

    private static final long serialVersionUID = 1L;

    /**
     * 标签名称
     */
    private String labelName;

    /**
     * 标签类型
     */
    private String labelType;

}
