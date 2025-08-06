package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.MessageCenterPageParam;
import com.wenge.model.entity.MessageCenter;
import com.wenge.model.utils.PageInfo;
import com.wg.appframe.core.bean.Result;

public interface MessageCenterService extends IService<MessageCenter> {

    Result add(MessageCenter messageCenter);

    Result delete(MessageCenter messageCenter);

    Result update(MessageCenter messageCenter);

    Result<Page<MessageCenter>> getListPage(MessageCenterPageParam param);

    Result<MessageCenter> getDataById(Long id);

}