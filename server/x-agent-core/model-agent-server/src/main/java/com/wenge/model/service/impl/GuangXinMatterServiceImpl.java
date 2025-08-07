package com.wenge.model.service.impl;


import cn.hutool.core.thread.ExecutorBuilder;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.entity.GuangXinMatter;
import com.wenge.model.enums.GuangXinMatterEnum;
import com.wenge.model.mapper.GuangXinMatterMapper;
import com.wenge.model.service.GuangXinMatterService;
import com.wenge.model.task.GuangxinTask;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;

import static com.wenge.model.entity.table.GuangXinMatterTableDef.GUANG_XIN_MATTER;

/**
 * Description: 关芯智巡的事项判别表服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-19 10:44:19
 *
 */
@Service
@Slf4j
public class GuangXinMatterServiceImpl extends ServiceImpl<GuangXinMatterMapper, GuangXinMatter> implements GuangXinMatterService {

	@Autowired
	private GuangxinTask guangxinTask;

	// 线程池
	private final ThreadPoolExecutor POOL = ExecutorBuilder.create().setCorePoolSize(8).setMaxPoolSize(8).setWorkQueue(new LinkedBlockingDeque<>()).build();


	/**
	 * 	关芯智巡的事项判别表数据库处理类
	 */
	@Autowired
	private GuangXinMatterMapper guangXinMatterMapper;

	@Override
	public Result addGuangXinMatter(GuangXinMatter guangXinMatter){
		save(guangXinMatter);
		return Result.success();
	}

	@Override
	public Result getGuangXinMatterList(GuangXinMatter guangXinMatter){
		return Result.success(null);
	}

	@Override
	public Result updateGuangXinMatter(GuangXinMatter guangXinMatter){
		updateById(guangXinMatter);
		return Result.success();
	}

	@Override
	public Result deleteGuangXinMatter(List<String> idList){
		removeByIds(idList);
		return Result.success();
	}

	@Override
	public List<GuangXinMatter> getNotExecuteMatter() {
		return Wrappers.of(mapper)
				.where(GUANG_XIN_MATTER.ERROR_COUNT.le(3))
				.and(true, and -> {
					and.or(GUANG_XIN_MATTER.STATUS.eq(GuangXinMatterEnum.NOT_START.getCode()));
					and.or(GUANG_XIN_MATTER.STATUS.eq(GuangXinMatterEnum.PARAM_NOT_FULL.getCode()));
					and.or(GUANG_XIN_MATTER.STATUS.eq(GuangXinMatterEnum.FAIL.getCode()));
					and.or(GUANG_XIN_MATTER.STATUS.eq(GuangXinMatterEnum.NOTIFY_FAIL.getCode()));
				}).list();
	}

	@Override
	public List<GuangXinMatter> getGxMatterDetail(String dataId) {
        return Wrappers.of(mapper)
                .where(GUANG_XIN_MATTER.DATA_ID.eq(dataId))
                .orderBy(GUANG_XIN_MATTER.CREATE_TIME.desc())
                .list();
	}

	@Override
	public void runMatterTask(GuangXinMatter item) {
		POOL.execute(()-> guangxinTask.handleMatter(item));
	}
}