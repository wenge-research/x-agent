package com.wenge.model.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.MessageCenterPageParam;
import com.wenge.model.entity.ApplicationPublishRecord;
import com.wenge.model.entity.MessageCenter;
import com.wenge.model.mapper.MessageCenterMapper;
import com.wenge.model.service.MessageCenterService;
import com.wenge.model.utils.PageInfo;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.wenge.model.entity.table.MessageCenterTableDef.MESSAGE_CENTER;

@Service
@Slf4j
public class MessageCenterServiceImpl extends ServiceImpl<MessageCenterMapper, MessageCenter> implements MessageCenterService {

    @Override
    public Result add(MessageCenter messageCenter) {
        if (messageCenter == null) {
            return Result.fail("参数错误");
        }
        return Result.success(this.save(messageCenter));
    }

    @Override
    public Result delete(MessageCenter messageCenter) {
        if (messageCenter == null) {
            return Result.fail("参数错误");
        }
        if (this.removeByIds(messageCenter.getDelIds())) {
            return Result.success();
        }
        return Result.fail();
    }

    @Override
    public Result update(MessageCenter messageCenter) {
        if (messageCenter == null || messageCenter.getId() == null) {
            return Result.fail("参数错误");
        }
        return Result.success(this.update(messageCenter));
    }

    @Override
    public Result<Page<MessageCenter>> getListPage(MessageCenterPageParam param) {
        if (param == null) {
            return Result.fail("参数错误");
        }
        if (param.getPageNo() == null) {
            param.setPageNo(1);
        }
        if (param.getPageSize() == null) {
            param.setPageSize(10);
        }
        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if (tokenUserInfo == null) {
            return Result.fail("用户信息获取失败");
        }
        Wrappers wrappers = Wrappers.init()
                .where(MESSAGE_CENTER.MESSAGE_USER_ID.eq(tokenUserInfo.getId()))
                .and(MESSAGE_CENTER.STATUS.eq(0))
                .and(param.getMessageStatus() != null, MESSAGE_CENTER.MESSAGE_STATUS.eq(param.getMessageStatus()))
                .orderBy(MESSAGE_CENTER.CREATE_TIME.desc());
        Page<MessageCenter> page = page(Page.of(param.getPageNo(), param.getPageSize()), wrappers);
        return Result.success(page);
    }

    @Override
    public Result<MessageCenter> getDataById(Long id) {

        return null;
    }
}