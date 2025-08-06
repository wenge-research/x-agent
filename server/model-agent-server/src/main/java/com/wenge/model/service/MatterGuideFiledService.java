package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.MatterFiledInfoParam;
import com.wenge.model.dto.result.AllFieldsResult;
import com.wenge.model.dto.result.MatterGuideFormResult;
import com.wenge.model.entity.MatterGuideFiled;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.exception.WosException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MatterGuideFiledService extends IService<MatterGuideFiled> {

    Result getFormInfo(MatterFiledInfoParam param);

    Result getFormInfoNew(MatterFiledInfoParam param);


    Result uploadPic(MultipartFile file) throws WosException;

    Result uploadYYZZPic(MultipartFile file) throws WosException;

    MinioInfoResult uploadFile(MultipartFile file, String filName);

    Result getTableFields(StringParam matterId);

    Result getSearchFields(StringParam matterId);

    Result<List<AllFieldsResult>> getAllFields(StringParam matterId);

    Result addFields(List<MatterGuideFiled> filedList);


    Result checkNameCodeExists(MatterFiledInfoParam param);

    Result<MatterGuideFormResult> getMatterGuideForm(MatterFiledInfoParam param);
}