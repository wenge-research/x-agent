package com.wenge.model.dto.param;

import com.wenge.model.dto.result.CollectField;
import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CollectConfigEditParam extends WGParam {

    private static final long serialVersionUID = 1293748417978584121L;

    /**
     * 数据集的数据库配置 id
     */
    private String configId;



    /**
     * 数据集的属性字段配置
     */
    private List<CollectField> fields;

    /**
     * 数据表名称
     */
    private String configName;

    /**
     * 数据表描述
     */
    private String describe;

}
