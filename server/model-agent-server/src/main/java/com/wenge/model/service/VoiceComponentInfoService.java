package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.VoiceComponentInfoDeleteParam;
import com.wenge.model.dto.param.VoiceComponentInfoPageParam;
import com.wenge.model.entity.VoiceComponentInfo;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.EmptyParam;
import com.wg.appframe.wos.exception.WosException;
import org.springframework.web.multipart.MultipartFile;

public interface VoiceComponentInfoService extends IService<VoiceComponentInfo> {

    Result<Page<VoiceComponentInfo>> getList(VoiceComponentInfoPageParam param);

    Result addInfo(VoiceComponentInfo param);

    Result update(VoiceComponentInfo param);

    Result deleteByIds(VoiceComponentInfoDeleteParam param);

    Result<VoiceComponentInfo> getDetail(Integer id);

    Result uploadPic(MultipartFile file) throws WosException;

    Result getTtsTags(EmptyParam param);

    Result setPreset(VoiceComponentInfo param);

    Result flush();
}