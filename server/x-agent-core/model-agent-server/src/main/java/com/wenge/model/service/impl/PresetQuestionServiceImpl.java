package com.wenge.model.service.impl;


import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.PresetQuestionPageParam;
import com.wenge.model.dto.param.PromptGenerateParam;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.PresetQuestion;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.mapper.ApplicationInfoMapper;
import com.wenge.model.mapper.PresetQuestionMapper;
import com.wenge.model.service.PresetQuestionService;
import com.wenge.model.utils.AnswerUtils;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.wenge.model.entity.table.ApplicationInfoTableDef.APPLICATION_INFO;
import static com.wenge.model.entity.table.PresetQuestionTableDef.PRESET_QUESTION;

/**
 * Description: 预置问题服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-18 14:01:21
 *
 */
@Service
@Slf4j
public class PresetQuestionServiceImpl extends ServiceImpl<PresetQuestionMapper, PresetQuestion> implements PresetQuestionService {
	/**
	 * 	预置问题数据库处理类
	 */
	@Autowired
	private PresetQuestionMapper presetQuestionMapper;

	@Autowired
	private AnswerUtils answerUtils;

	@Autowired
	private ApplicationInfoMapper applicationInfoMapper;

	@Value("${aiCommand.presetQuestion}")
	private String presetQuestionCommand;

	@Override
	public Result addPresetQuestion(PresetQuestion presetQuestion){
		if (StringUtils.isBlank(presetQuestion.getPresetId())) {
			presetQuestion.setPresetId(IdUtil.simpleUUID());
		}
		saveOrUpdate(presetQuestion);
		return Result.success();
	}

	@Override
	public Result getPresetQuestionList(PresetQuestionPageParam presetQuestion){
		Wrappers wrappers = Wrappers.init()
				.where(StringUtils.isNotBlank(presetQuestion.getType()), PRESET_QUESTION.TYPE.eq(presetQuestion.getType()))
				.and(StringUtils.isNotBlank(presetQuestion.getApplicationId()), PRESET_QUESTION.APPLICATION_ID.eq(presetQuestion.getApplicationId()));
		Page<PresetQuestion> page = page(Page.of(presetQuestion.getPageNo(), presetQuestion.getPageSize()), wrappers);
		return Result.success(page);
	}

	@Override
	public Result updatePresetQuestion(PresetQuestion presetQuestion){
		updateById(presetQuestion);
		return Result.success();
	}

	@Override
	public Result deletePresetQuestion(List<String> idList){
		removeByIds(idList);
		return Result.success();
	}

	@Override
	public List<PresetQuestion> getByAppIds(List<String> appIds) {
		if (CollectionUtils.isEmpty(appIds)) {
			return Lists.newArrayList();
		}
		return Wrappers.of(mapper)
				.where(
						PRESET_QUESTION.APPLICATION_ID.in(appIds))
				.and(PRESET_QUESTION.STATUS.eq(YesNoEnum.YES.getCode()))
				.list();
	}

	@Override
	public Result getPresetQuestionByAI(PromptGenerateParam param) {
		if (StringUtils.isBlank(param.getApplicationId())) {
			return Result.fail("应用id不能为空");
		}
		QueryWrapper wrapper = Wrappers.init()
				.and(StringUtils.isNotEmpty(param.getApplicationId()),
						APPLICATION_INFO.APPLICATION_ID.eq(param.getApplicationId()));
		List<ApplicationInfo> listApp= applicationInfoMapper.selectListByQuery(wrapper);
		ApplicationInfo applicationInfo = new ApplicationInfo();
		if(CollectionUtils.isNotEmpty(listApp)&& !listApp.isEmpty()){
			applicationInfo= listApp.get(0);
		}
		String presetQuestionCommand = this.presetQuestionCommand;
		if (Objects.isNull( param.getQuestionNum())) {
			// 默认推荐三个问题
			param.setQuestionNum(3);
		}
		presetQuestionCommand = presetQuestionCommand.replace("{applicationName}", applicationInfo.getApplicationName());
		presetQuestionCommand = presetQuestionCommand.replace("{introduce}", applicationInfo.getIntroduce());
		presetQuestionCommand = presetQuestionCommand.replace("{questionNum}", param.getQuestionNum().toString());

		DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
		String generateCommon = answerUtils.getGenerateCommon(null,
				presetQuestionCommand,
				new StepEntity(), null, null);
		int startIndex1 = generateCommon.indexOf("[");
		int endIndex1 = generateCommon.lastIndexOf("]");
		if (startIndex1 != -1&&endIndex1!= -1) {
			generateCommon= generateCommon.substring(startIndex1, endIndex1+1);
		} else {
			return Result.fail("大模式出错");
		}
		return Result.success(JSONUtil.toList(generateCommon, String.class));
	}
}