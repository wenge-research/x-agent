package com.wenge.model.dto.result;

import com.wenge.model.entity.MatterGuide;
import com.wenge.model.entity.MatterGuideFiled;
import com.wenge.model.entity.MatterGuideGroup;
import com.wg.appframe.core.dto.results.WGResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MatterGuideFormResult extends WGResult {

    private static final long serialVersionUID = 9213631006481323510L;

    /**
     * 事项信息
     */
    private MatterGuide matterGuide;

    /**
     * 表单信息
     */
    private List<FormConfig> formConfigList;


    @Data
    public static class FormConfig implements Serializable {

        private static final long serialVersionUID = -5275685766924978998L;

        /**
         * 分组
         */
        private MatterGuideGroup group;

        /**
         * 字段信息
         */
        private List<MatterGuideFiled> filedList;
    }

}
