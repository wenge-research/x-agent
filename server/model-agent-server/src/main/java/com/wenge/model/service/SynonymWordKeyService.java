package com.wenge.model.service;

import com.wenge.model.dto.param.ImportSynonymWordDataParam;
import com.wenge.model.dto.param.SynonymWordPageParam;
import com.wenge.model.entity.SynonymWord;
import com.wenge.model.entity.SynonymWordKey;
import com.wg.appframe.core.bean.Result;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface SynonymWordKeyService {

    /**
     * 添加同义词
     * @param synonymWordKey
     */
    public Result addSynonymWordKey(SynonymWordKey synonymWordKey);

    /**
     * 编辑同义词
     * @param synonymWordKey
     */
    public Result editSynonymWord(SynonymWordKey synonymWordKey);

    /**
     * 编辑同义词
     * @param idList
     */
    public Result deleteSynonymWords(List<String> idList);

    /**
     * 查询同义词详情
     * @param synonymWord
     */
    public Result queryDetails(SynonymWordKey synonymWord);

    /**
     * 查询同义词详情
     * @param synonymWordPageParam
     */
    public Result queryList(SynonymWordPageParam synonymWordPageParam);

    /**
     * 查询分类下拉列表
     * @return
     */
    public Result querySelect();


    /**
     * 根据同义词查询关键字
     * @param keyword
     * @return
     */
    public List<SynonymWord> queryBySynonymWord(String keyword);

    /**
     * 导入同义词
     * @param param
     * @return
     */
    Result<String> importSynonymWordData(ImportSynonymWordDataParam param);

    /**
     * 下载同义词模版
     * @param response
     */
    void downloadSynonymWordDataTemp(HttpServletResponse response);
}
