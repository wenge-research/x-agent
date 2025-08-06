package com.wenge.model.service;

import com.wenge.model.dto.param.MultiMediaDataPageParam;
import com.wenge.model.entity.MultiMediaData;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.ListStringParam;

public interface MultiMediaDataService {

    Result addFileData(MultiMediaData multiMediaData);


    Result deleteFileData(ListStringParam fileDataId);

    Result getFileDatas(MultiMediaDataPageParam param);

    Result<String> getDataByFileId(MultiMediaDataPageParam param);
}
