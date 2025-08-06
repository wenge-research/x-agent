package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.MatterGuideListParam;
import com.wenge.model.dto.param.MatterGuideParam;
import com.wenge.model.entity.MatterGuide;
import com.wenge.model.vo.MatterGuideListVo;
import com.wg.appframe.core.bean.Result;

import java.util.List;

public interface MatterGuideService extends IService<MatterGuide> {
    Result<List<MatterGuideListVo>> getList(MatterGuideListParam param);

    Result<List<MatterGuide>> getActiveList(MatterGuideListParam param);

    Result addMatter(MatterGuide matterGuide);

    Result editMatter(MatterGuide matterGuide);

    Result getMatterById(Long id);

    MatterGuide getMatterByMatterId(String matterId);

    Result getMatterList(MatterGuideParam matterGuide);

    Result deleteMatter(MatterGuideParam matterGuideParam);


    Result updateShowFlag(MatterGuide matterGuide);

    /**
     * 获取事项名称，给对话使用
     *
     * @param applicationId
     * @return
     */
    List<MatterGuide> getMatterName(String applicationId, String sceneId);

    /**
     * 通过事项id获取事项信息
     * @param matterIds
     * @return
     */
    List<MatterGuide> getMatterByMatterIds(List<String> matterIds);

    /**
     * 获取事项名称，给对话使用
     *
     * @param sceneId
     * @return
     */
    List<MatterGuide> getMatterName(String sceneId);
}