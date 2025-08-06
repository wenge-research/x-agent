package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.MyFavoriteQueryParam;
import com.wenge.model.entity.MyFavorite;
import com.wg.appframe.core.bean.Result;

public interface MyFavoriteService extends IService<MyFavorite> {

    Result editMyFavorite(MyFavoriteQueryParam param);

    Result getMyFavoriteByApplicationId(String applicationId);

    Result<Page<MyFavorite>> getAllMyFavorite(MyFavoriteQueryParam param);

}