package com.wenge.model.service;

import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.MatterInfoPageParam;
import com.wenge.model.dto.param.MatterInfoUpdateParam;
import com.wenge.model.entity.MatterGuideInfo;
import com.wenge.model.utils.PageInfo;
import com.wenge.model.vo.MatterGuideInfoPageVo;
import com.wg.appframe.core.bean.Result;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface MatterGuideInfoService extends IService<MatterGuideInfo> {

    Result<PageInfo<MatterGuideInfoPageVo>> getList(MatterInfoPageParam param);


    Result<PageInfo<JSONObject>> getListNew(MatterInfoPageParam param);

    Result getDetail(String infoId);

    Result getDetailNew(String infoId);

    Result updateInfo(MatterInfoUpdateParam param);

    void export(MatterInfoPageParam param, HttpServletResponse response) throws IllegalAccessException, IOException;

    void exportNew(MatterInfoPageParam param, HttpServletResponse response) throws IllegalAccessException, IOException;



    /**
     * @description: 事项处置-根据事项id动态获取检索条件
     * @author: caohaifeng
     * @date: 2024/10/9 14:30
     * @param: [matterId]
     * @return: com.wg.appframe.core.bean.Result
     **/
    Result getSearchFiledByMatterId(String matterId);

    /**
     * @description: 事项处置-根据事项id动态获取表头
     * @author: caohaifeng
     * @date: 2024/10/9 14:30
     * @param: [matterId]
     * @return: com.wg.appframe.core.bean.Result
     **/
    Result getTableHeadFiledByMatterId(String matterId);
}