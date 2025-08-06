package com.wenge.oauth.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Lists;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.oauth.dto.param.AuthChannelPageParam;
import com.wenge.oauth.dto.result.AuthChannelResult;
import com.wenge.oauth.entity.AuthChannel;
import com.wenge.oauth.mapper.AuthChannelMapper;
import com.wenge.oauth.service.AuthChannelService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.wenge.oauth.entity.table.AuthChannelTableDef.AUTH_CHANNEL;

/**
 * Description: 认证方式渠道配置表服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-02 15:28:16
 *
 */
@Service
@Slf4j
public class AuthChannelServiceImpl extends ServiceImpl<AuthChannelMapper, AuthChannel> implements AuthChannelService {
	/**
	 * 	认证方式渠道配置表数据库处理类
	 */
	@Autowired
	private AuthChannelMapper authChannelMapper;

	@Override
	public Result addAuthChannel(AuthChannel authChannel){
		if(StringUtils.isBlank(authChannel.getAuthChannelId()) && authChannel.getId() == null){
			authChannel.setAuthChannelId(IdUtil.simpleUUID());
		}
		authChannel.setChannelCode("h5_we_chat_office");
		authChannel.setChannelName("微信公众号");
		authChannel.setClientType("H5");
		authChannel.setChannelCode("h5_we_chat_office");
		saveOrUpdate(authChannel);
		return Result.success();
	}

	@Override
	public Result getAuthChannelList(AuthChannelPageParam authChannel){
		Wrappers<Object> wrappers = Wrappers.init();
		Page<AuthChannel> page = page(Page.of(authChannel.getPageNo(), authChannel.getPageSize()), wrappers);
		return Result.success(page);
	}

	@Override
	public Result updateAuthChannel(AuthChannel authChannel){
		updateById(authChannel);
		return Result.success();
	}

	@Override
	public Result deleteAuthChannel(List<String> idList){
		removeByIds(idList);
		return Result.success();
	}

	@Override
	public Result<List<AuthChannelResult>> getAuthChannels(AuthChannelPageParam authChannel) {
		if (StringUtils.isBlank(authChannel.getClientType())) {
			return Result.success(Lists.newArrayList());
		}
		List<AuthChannel> list = QueryChain.of(mapper)
				.where(AUTH_CHANNEL.APPLICATION_ID.eq(authChannel.getApplicationId()))
				.and(AUTH_CHANNEL.CLIENT_TYPE.eq(authChannel.getClientType()))
				.list();
		List<AuthChannelResult> channelResults = list.stream().map(p -> {
			AuthChannelResult authChannelResult = new AuthChannelResult();
			authChannelResult.setChannelName(p.getChannelName());
			authChannelResult.setAuthChannelId(p.getAuthChannelId());
			authChannelResult.setChannelCode(p.getChannelCode());
			return authChannelResult;
		}).collect(Collectors.toList());
		return Result.success(channelResults);
	}

	@Override
	public List<AuthChannel> getByIds(List<String> AuthChannelIdList) {
		if (CollectionUtil.isEmpty(AuthChannelIdList)) {
			return Lists.newArrayList();
		}

        return QueryChain.of(mapper)
				.where(AUTH_CHANNEL.AUTH_CHANNEL_ID.in(AuthChannelIdList))
				.list();
	}

	@Override
	public AuthChannel getAuthChannelByApplicationId(String applicationId, String channelCode) {
		if (applicationId == null) {
			log.warn("参数异常");
			return null;
		}
		if("pc_gov".equals(channelCode)){
			List<AuthChannel> list = QueryChain.of(mapper)
//					.where(AUTH_CHANNEL.APPLICATION_ID.eq(applicationId))
					.where(AUTH_CHANNEL.CHANNEL_CODE.eq("pc_gov"))
					.and(AUTH_CHANNEL.CLIENT_TYPE.eq("PC"))
					.list();
			return (list != null && list.size()>0) ? list.get(0) : null;
		}else {
			List<AuthChannel> list = QueryChain.of(mapper)
					.where(AUTH_CHANNEL.APPLICATION_ID.eq(applicationId))
					.and(AUTH_CHANNEL.CLIENT_TYPE.eq("H5"))
					.list();
			return (list != null && list.size()>0) ? list.get(0) : null;
		}
	}


}