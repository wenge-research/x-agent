package com.wenge.model.dto.param;

import com.wenge.model.entity.SceneMatterGroupRef;
import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询分组与事项关联的参数对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GroupMatterRefAddParam extends WGParam {

    private static final long serialVersionUID = -4267139453650653462L;

    /**
     * 场景id
     */
    private String sceneId;

    /**
     * 分组事项关联对象
     */
    private List<GroupMatter> groupMatterList;


    /**
     * 分组事项关联对象
     */
    @Data
    public static class GroupMatter implements Serializable {

        private static final long serialVersionUID = 4966661613553949101L;

        /**
         *  分组id
         */
        private String groupId;

        /**
         * 事项集合
         */
        private List<SceneMatterGroupRef> matterList;
    }
}
