package com.wenge.model.service;

import com.wenge.model.dto.param.CorrectWordPageParam;
import com.wenge.model.dto.param.ImportCorrectWordDataParam;
import com.wenge.model.entity.CorrectWord;
import com.wenge.model.entity.CorrectWordKey;
import com.wg.appframe.core.bean.Result;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface CorrectWordKeyService {

    /**
     * 添加同义词
     * @param correctWordKey
     */
    public Result addCorrectWord(CorrectWordKey correctWordKey);

    /**
     * 编辑同义词
     * @param correctWordKey
     */
    public Result editCorrectWord(CorrectWordKey correctWordKey);

    /**
     * 编辑同义词
     * @param idList
     */
    public Result deleteCorrectWord(List<String> idList);

    /**
     * 查询同义词详情
     * @param correctWordKey
     */
    public Result queryDetails(CorrectWordKey correctWordKey);

    /**
     * 查询同义词详情
     * @param correctWordPageParam
     */
    public Result queryList(CorrectWordPageParam correctWordPageParam);

    /**
     * 查询分类下拉列表
     * @return
     */
    public Result querySelect();

    /**
     * 根据纠错词查询关键字
     * @param keyword
     * @return
     */
    public List<CorrectWord> queryBySynonymWord(String keyword);

    /**
     * 导入纠错词
     * @param param
     * @return
     */
    Result<String> importCorrectWordData(ImportCorrectWordDataParam param);

    void downloadCorrectWordDataTemp(HttpServletResponse response);
}
