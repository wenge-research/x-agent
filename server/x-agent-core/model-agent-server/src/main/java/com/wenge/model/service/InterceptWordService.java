package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.ImportInterceptWordDataParam;
import com.wenge.model.dto.param.InterceptWordPageParam;
import com.wenge.model.entity.InterceptWord;
import com.wg.appframe.core.bean.Result;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Description: 拦截词服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-09 21:15:57
 *
 */
public interface InterceptWordService extends IService<InterceptWord> {

    Result addInterceptWord(InterceptWord interceptWord);

    Result getInterceptWordList(InterceptWordPageParam interceptWord);

    // 根据敏感词拦截库名称查询对应的拦截词
    List<InterceptWord> getInterceptWordListByHouseName(String interceptWord);

    Result updateInterceptWord(InterceptWord interceptWord);

    Result deleteInterceptWord(List<String> idList);

    Result updateStatus(InterceptWord interceptWord);

    Result getInterceptWordTypeList();

    Result getInterceptWordHandlingMethodList();

    Result importInterceptWordData(ImportInterceptWordDataParam param);


    long updateInterceptWordData(Long interceptWordHouseId);

    void downloadInterceptWordDataTemp(HttpServletResponse response);


    List<InterceptWord> getAllWord();

    /**
     * 根据拦截词库id查询拦截词
     * @param houseIds
     * @return
     */
    List<InterceptWord> getInterceptWordListByHouseIds(List<Long> houseIds);

}