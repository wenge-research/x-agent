package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PluginParam extends WgPageInfo {

    private static final long serialVersionUID = 3037384264619002498L;

    private String componentName;

    private Integer publishAppStore; //0-未发布 1-已发布 2-待审核

    private Integer status; // 是否启用

    private String labels; //插件标签

    private Integer pluginType; // 插件类型

    private Integer type=1;

    private String createUser = "";

    private String ownerType; // 插件归属类型

    private Integer id; // 插件id
}
