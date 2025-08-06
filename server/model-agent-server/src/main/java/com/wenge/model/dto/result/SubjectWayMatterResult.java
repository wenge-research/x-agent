package com.wenge.model.dto.result;

import com.wenge.model.entity.MatterGuide;
import com.wenge.model.entity.SceneManagement;
import com.wg.appframe.core.dto.results.WGResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubjectWayMatterResult extends WGResult {

    private static final long serialVersionUID = 736826455016160737L;

    /**
     * 场景信息
     */
    private SceneManagement sceneManagement;

    /**
     * 事项列表
     */
    private List<MatterGuide> matterNameList;
}
