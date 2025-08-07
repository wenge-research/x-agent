package com.wenge.model.service;


import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.LlmManufacturerModelParam;
import com.wenge.model.dto.result.LlmManufacturerResult;
import com.wenge.model.entity.LlmManufacturerModel;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;

import java.util.List;

public interface LlmManufacturerModelService extends IService<LlmManufacturerModel> {

    /**
     * 获取合作商
     * @return
     */
    Result<List<LlmManufacturerResult>> getLlmManufacturer();

    /**
     * 获取合作商下的model
     * @param param
     * @return
     */
    Result<List<String>> getLlmManufacturerModelList(StringParam param);

    Result<LlmManufacturerModel>  getLlmManufacturerModel(LlmManufacturerModelParam param);

    /**
     * 获取所有的模型数据
     * @return
     */
    List<LlmManufacturerModel> getLlmManufacturerList();
}
