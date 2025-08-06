package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.MatterGuideGroupParam;
import com.wenge.model.entity.MatterGuideGroup;
import com.wenge.model.entity.MatterGuideType;
import com.wg.appframe.core.bean.Result;

import java.util.List;

public interface MatterGuideGroupService extends IService<MatterGuideGroup> {

    /**
     * @description: 添加字段分组信息
     * @author: caohaifeng
     * @date: 2024/9/4 14:08
     **/
    Result addMatterGuideGroup(MatterGuideGroup matterGuideGroup);


    Result addOrUpdateMatterGroupList(List<MatterGuideGroup> matterGuideGroupList);



    /**
     * @description: 获取所有的字段分组信息
     * @author: caohaifeng
     * @date: 2024/8/19 10:29
     **/
    Result<List<MatterGuideGroup>> getMatterGuideGroupList(MatterGuideGroupParam matterGuideGroupParam);


    /**
     * @description: 获取所有的字段分组信息
     * @author: caohaifeng
     * @date: 2024/8/19 10:31
     **/
    Result delMatterGuideGroupById(Long id);


    /**
     * @description: 获取所有的事项类型信息
     * @author: caohaifeng
     * @date: 2024/8/22 10:52
     **/
    Result getMatterGuideTypeList(MatterGuideType matterGuideType);

    /**
     * @param sceneId
     * @return
     * @description: 根据场景ID获取事项分组信息
     */
    List<MatterGuideGroup> getBySceneIId(String sceneId);

    /**
     * @description: 根据事项ID获取事项分组信息
     * @param matterId
     * @return
     */
    List<MatterGuideGroup> getByMatterId(String matterId);

}