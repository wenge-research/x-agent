package com.wenge.model.service;

import com.wenge.model.dto.param.FileDataPageParam;
import com.wenge.model.entity.FileData;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.ListStringParam;

import java.util.List;

public interface FileDataService {

    Result addFileData(FileData fileData);


    Result deleteFileData(ListStringParam fileDataId);

    Result getFileDatas(FileDataPageParam param);

    List<FileData> getFileDataKnowledges(FileDataPageParam param);

    Result<String> getDataByFileId(FileDataPageParam param);

    Result copyFileData(FileData fileData);

    Result searchDataByKeywords(FileDataPageParam param);
}
