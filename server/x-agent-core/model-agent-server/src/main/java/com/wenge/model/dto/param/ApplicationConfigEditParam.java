package com.wenge.model.dto.param;

import com.wenge.model.entity.ApplicationPlugin;
import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationConfigEditParam extends WGParam {

    private static final long serialVersionUID = 9015849189912056201L;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 插件id列表
     */
    private List<ApplicationPlugin> pluginList;
}
