package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.entity.GuangXinMatter;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 关芯智巡的事项判别表服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-19 10:44:19
 *
 */
public interface GuangXinMatterService extends IService<GuangXinMatter> {

    Result addGuangXinMatter(GuangXinMatter guangXinMatter);

    Result getGuangXinMatterList(GuangXinMatter guangXinMatter);

    Result updateGuangXinMatter(GuangXinMatter guangXinMatter);

    Result deleteGuangXinMatter(List<String> idList);

    /**
     * 检索未执行成功的事项
     */
    List<GuangXinMatter> getNotExecuteMatter();

    /**
     * 检索未执行成功的事项
     */
    List<GuangXinMatter> getGxMatterDetail(String dataId);

    /**
     * 运行事项任务，这是单个任务
     * @param item
     */
    void runMatterTask(GuangXinMatter item);
}