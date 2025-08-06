package com.wenge.model.dto.param;

import com.alibaba.fastjson2.annotation.JSONField;
import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class FileAndLinkAddParam extends WGParam {

    private static final long serialVersionUID = -7391139066572564370L;

    private String foldersId;

    /**
     * 文档类型
     */
    private Integer type;

    @JSONField(serialize = false)
    private List<FileAddWebLinkParam> files;


}
