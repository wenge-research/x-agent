package com.wenge.oauth.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.oauth.entity.SmsTencentRecord;
import com.wenge.oauth.mapper.SmsTencentRecordMapper;
import com.wenge.oauth.service.SmsTencentRecordService;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 腾讯云短信记录表服务实现类
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-02-08 18:11:08
 *
 */
@Service
@Slf4j
public class SmsTencentRecordServiceImpl extends ServiceImpl<SmsTencentRecordMapper, SmsTencentRecord> implements SmsTencentRecordService {
	/**
	 * 	腾讯云短信记录表数据库处理类
	 */
	@Autowired
	private SmsTencentRecordMapper smsTencentRecordMapper;

	@Override
	public void addSmsTencentRecord(List<SmsTencentRecord> smsTencentRecords){
		if (CollectionUtil.isEmpty(smsTencentRecords)) {
			return;
		}
		saveBatch(smsTencentRecords);
	}

}