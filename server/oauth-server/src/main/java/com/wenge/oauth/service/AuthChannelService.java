package com.wenge.oauth.service;

import com.mybatisflex.core.service.IService;
import com.wenge.oauth.dto.param.AuthChannelPageParam;
import com.wenge.oauth.dto.result.AuthChannelResult;
import com.wenge.oauth.entity.AuthChannel;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 认证方式渠道配置表服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-02 15:28:16
 *
 */
public interface AuthChannelService extends IService<AuthChannel> {

    Result addAuthChannel(AuthChannel authChannel);

    Result getAuthChannelList(AuthChannelPageParam authChannel);

    Result updateAuthChannel(AuthChannel authChannel);

    Result deleteAuthChannel(List<String> idList);

    Result<List<AuthChannelResult>> getAuthChannels(AuthChannelPageParam authChannel);

    List<AuthChannel> getByIds(List<String> AuthChannelIdList);

    AuthChannel getAuthChannelByApplicationId(String applicationId, String channelCode);

}