package com.wenge.oauth.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.oauth.entity.OperaLog;
import com.wenge.oauth.mapper.OperaLogMapper;
import com.wenge.oauth.service.OperaLogService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wenge.oauth.entity.table.OperaLogTableDef.OPERA_LOG;

/**
 * Description: 操作日志服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-27 16:17:49
 *
 */
@Service
@Slf4j
public class OperaLogServiceImpl extends ServiceImpl<OperaLogMapper, OperaLog> implements OperaLogService {
	/**
	 * 	操作日志数据库处理类
	 */
	@Autowired
	private OperaLogMapper operaLogMapper;

	@Override
	public Result addOperaLog(OperaLog operaLog){
		save(operaLog);
		return Result.success();
	}

	@Override
	public Result getOperaLogList(OperaLog operaLog){
		return Result.success(null);
	}

	@Override
    public Result updateOperaLog(OperaLog operaLog){
        updateById(operaLog);
        return Result.success();
	}

	@Override
	public Result deleteOperaLog(List<String> idList){
		removeByIds(idList);

		return Result.success();
	}

	@Override
	public Long getOperaLogCount(OperaLog operaLog) {

		return Wrappers.of(mapper)
				.where(StringUtils.isNotBlank(operaLog.getOperUrl()), OPERA_LOG.OPER_URL.eq(operaLog.getOperUrl()))
				.and(CollectionUtil.isNotEmpty(operaLog.getOperUrls()), OPERA_LOG.OPER_URL.in(operaLog.getOperUrls()))
				.count();
	}
}