package com.wenge.model.service;


import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.ApplicationInfoPageParam;
import com.wenge.model.entity.ApplicationPublishRecord;
import com.wenge.model.entity.table.ApplicationInfoTableDef;
import com.wenge.model.mapper.ApplicationPublishRecordMapper;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import static com.wenge.model.entity.table.ApplicationInfoTableDef.APPLICATION_INFO;
import static com.wenge.model.entity.table.ApplicationPublishRecordTableDef.APPLICATION_PUBLISH_RECORD;

@Service
@Slf4j
public class ApplicationPublishRecordServiceImpl extends ServiceImpl<ApplicationPublishRecordMapper, ApplicationPublishRecord> implements ApplicationPublishRecordService {

	@Override
	public Result<Page<ApplicationPublishRecord>> getAppPublishRecordList(ApplicationInfoPageParam applicationInfo) {
		if (applicationInfo.getPageNo() == null) {
			applicationInfo.setPageNo(1);
		}
		if (applicationInfo.getPageSize() == null) {
			applicationInfo.setPageSize(200);
		}
		Wrappers wrappers = Wrappers.init()
				.where(APPLICATION_PUBLISH_RECORD.APPLICATION_ID.eq(applicationInfo.getApplicationId()))
				.and(APPLICATION_PUBLISH_RECORD.MESSAGE_SOURCE.eq(applicationInfo.getMessageSource()))
				.and(APPLICATION_PUBLISH_RECORD.DELETE_FLAG.eq(0))
				.orderBy(APPLICATION_PUBLISH_RECORD.CREATE_TIME.desc());
		Page<ApplicationPublishRecord> page = page(Page.of(applicationInfo.getPageNo(), applicationInfo.getPageSize()), wrappers);
		return Result.success(page);
	}
}