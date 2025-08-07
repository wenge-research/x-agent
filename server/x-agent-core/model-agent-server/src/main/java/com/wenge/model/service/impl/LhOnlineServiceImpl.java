package com.wenge.model.service.impl;


import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.entity.LhOnline;
import com.wenge.model.mapper.LhOnlineMapper;
import com.wenge.model.service.LhOnlineService;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wenge.model.entity.table.LhOnlineTableDef.LH_ONLINE;

/**
 * Description: 龙华政府在线服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-20 15:57:11
 *
 */
@Service
@Slf4j
public class LhOnlineServiceImpl extends ServiceImpl<LhOnlineMapper, LhOnline> implements LhOnlineService {
	@Override
	public List<LhOnline> getActiveList() {
		return Wrappers.of(mapper)
				.where(LH_ONLINE.STATUS.eq("active"))
				.list();
	}
}