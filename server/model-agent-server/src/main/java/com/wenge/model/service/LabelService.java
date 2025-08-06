package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.LabelParam;
import com.wenge.model.entity.Label;

import java.util.List;

public interface LabelService extends IService<Label> {

    /**
     * 添加标签
     * @param label
     */
    void add(Label label);

    /**
     * 查询label列表
     * @param label
     * @return
     */
    List<Label> getLabels(Label label);

    /**
     * 查询label列表
     * @param labelType
     * @param labelNames
     * @return
     */
    List<Label> getLabels(String labelType,  List<String> labelNames);

    /**
     * 根据关键词模糊查询
     * @param label
     * @return
     */
    Page<Label> getLabelsByWord(LabelParam label);

    public void batchAdd(List<Label> labels, String labelType);
}
