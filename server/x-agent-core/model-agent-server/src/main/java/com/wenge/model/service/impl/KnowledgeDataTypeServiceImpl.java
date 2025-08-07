package com.wenge.model.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Maps;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.KnowledgeDataTypeParam;
import com.wenge.model.dto.param.UnionParam;
import com.wenge.model.entity.Dialogue;
import com.wenge.model.entity.KnowledgeData;
import com.wenge.model.entity.KnowledgeDataType;
import com.wenge.model.mapper.DataSourceParserInfoMapper;
import com.wenge.model.mapper.FileMapper;
import com.wenge.model.mapper.KnowledgeDataTypeMapper;
import com.wenge.model.mapper.UrlParserInfoMapper;
import com.wenge.model.mapper.es.KnowledgeDataMapper;
import com.wenge.model.service.KnowledgeDataTypeService;
import com.wenge.model.vo.StructureParserVo;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.ListStringParam;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.FileTableDef.FILE;
import static com.wenge.model.entity.table.KnowledgeDataTypeTableDef.KNOWLEDGE_DATA_TYPE;
import static com.wenge.model.entity.table.UrlParserInfoTableDef.URL_PARSER_INFO;

/**
 * Description: 知识数据分类服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-18 18:21:03
 *
 */
@Service
@Slf4j
public class KnowledgeDataTypeServiceImpl extends ServiceImpl<KnowledgeDataTypeMapper, KnowledgeDataType> implements KnowledgeDataTypeService {
	/**
	 * 	知识数据分类数据库处理类
	 */
	@Autowired
	private KnowledgeDataTypeMapper knowledgeDataTypeMapper;

	@Autowired
	private KnowledgeDataMapper knowledgeDataMapper;

	@Autowired
	private FileMapper fileMapper;

	@Autowired
	private UrlParserInfoMapper urlParserInfoMapper;

	@Autowired
	private DataSourceParserInfoMapper dataSourceParserInfoMapper;

	@Override
	public Result addKnowledgeDataType(KnowledgeDataType knowledgeDataType){
		saveOrUpdate(knowledgeDataType);
		return Result.success();
	}

	@Override
	public Result getKnowledgeDataTypeList(KnowledgeDataTypeParam param){
		Wrappers<Object> wrappers = Wrappers.init()
				.where(null != param.getStatus(), KNOWLEDGE_DATA_TYPE.STATUS.eq(param.getStatus()))
				.and(StringUtils.isNotBlank(param.getType()), KNOWLEDGE_DATA_TYPE.TYPE.like(param.getType()))
				.and(StringUtils.isNotBlank(param.getKnowledgeId()), KNOWLEDGE_DATA_TYPE.KNOWLEDGE_ID.eq(param.getKnowledgeId()));
		Page<KnowledgeDataType> page = page(Page.of(param.getPageNo(), 9999), wrappers);
		List<KnowledgeDataType> records = page.getRecords();
		if (CollectionUtil.isNotEmpty(records)) {
			Map<Long, List<KnowledgeDataType>> listMap = records.stream().collect(Collectors.groupingBy(KnowledgeDataType::getPid));
			records.forEach(knowledgeDataType -> {
				knowledgeDataType.setChildren(listMap.get(knowledgeDataType.getId()));
			});
			records = records.stream().filter(p -> p.getPid() == 0).collect(Collectors.toList());
			page.setRecords(records);
		}
		return Result.success(page);
	}

	@Override
	public Result updateKnowledgeDataType(KnowledgeDataType knowledgeDataType){
		updateById(knowledgeDataType);
		return Result.success();
	}

	@Override
	public Result deleteKnowledgeDataType(ListStringParam idList){
		// 如果目录下面有数据，不允许删除
		LambdaEsQueryWrapper<KnowledgeData> wrapper = new LambdaEsQueryWrapper<>();
		wrapper.in(KnowledgeData::getCategory, idList.getParam());
		wrapper.eq(KnowledgeData::getDelStatus, "0");
		Long qaCount = knowledgeDataMapper.selectCount(wrapper);
		if (qaCount > 0) {
			return Result.fail("目录下有QA数据，不允许删除");
		}
		Wrappers<Object> queryWrapper = Wrappers.init()
				.where(FILE.FOLDERS_ID.in(idList.getParam()))
				.and(FILE.DELETE_FLAG.eq("0"));
		long fileCount = fileMapper.selectCountByQuery(queryWrapper);
		if (fileCount > 0) {
			return Result.fail("目录下有文件[多媒体]数据，不允许删除");
		}
		Wrappers<Object> urlWrapper = Wrappers.init()
				.where(URL_PARSER_INFO.FOLDERS_ID.in(idList.getParam()))
				.and(URL_PARSER_INFO.DELETE_FLAG.eq("0"));
		long urlCount = urlParserInfoMapper.selectCountByQuery(urlWrapper);
		if (urlCount > 0) {
			return Result.fail("目录下有URL数据，不允许删除");
		}
		UnionParam param = new UnionParam();
		param.setDeleteFlag(0);
		Integer dataCount = dataSourceParserInfoMapper.unionDataCount(
				null, param, idList.getParam());
		if (dataCount > 0) {
			return Result.fail("目录下有结构化数据，不允许删除");
		}
		if (CollectionUtil.isNotEmpty(idList.getParam())) {
			removeByIds(idList.getParam());
		}
		return Result.success();
	}

	@Override
	public Map<Long, String> getTypeMap(String knowledgeId) {
		if (StringUtils.isBlank(knowledgeId)) {
			return Maps.newHashMap();
		}
		Wrappers<Object> wrappers = Wrappers.init()
				.where(KNOWLEDGE_DATA_TYPE.KNOWLEDGE_ID.eq(knowledgeId)).and(KNOWLEDGE_DATA_TYPE.DELETE_FLAG.eq(0));
		List<KnowledgeDataType> list = list(wrappers);
		if (CollectionUtil.isNotEmpty(list)) {
			Map<Long, String> typeMap = list.stream().collect(Collectors.toMap(
					KnowledgeDataType::getId,
					KnowledgeDataType::getType,
					(k1, k2) -> k1,
					Maps::newHashMap
			));
			return typeMap;
		}
		return Maps.newHashMap();
	}


	@Override
	public List<KnowledgeDataType> getTreeById(Long id, List<KnowledgeDataType> dataTypeList) {
		if (CollectionUtil.isEmpty(dataTypeList) || Objects.isNull(id)) {
			return new ArrayList<>();
		}
		Optional<KnowledgeDataType> any = dataTypeList.stream().filter(v -> Objects.equals(v.getId(), id)).findAny();
		if (!any.isPresent()) {
			throw new RuntimeException("获取当前分类失败");
		}
		// 获取当前分类
		KnowledgeDataType currentDataType = any.get();

		return findChildren(currentDataType, dataTypeList);
	}


	/**
	 * 递归获取当前分类下所有子分类
	 * @param dataType
	 * @param treeList
	 * @return
	 */
	private List<KnowledgeDataType> findChildren(KnowledgeDataType dataType, List<KnowledgeDataType> treeList) {
		List<KnowledgeDataType> result = new ArrayList<>();
		// 添加当前分类
		result.add(dataType);
		// 获取当前文件夹下所有分类
		List<KnowledgeDataType> children = treeList.stream().filter(topicTree -> Objects.equals(topicTree.getPid(), dataType.getId())).collect(Collectors.toList());
		children.forEach(topicTree -> {
			// 递归获取当前分类下所有分类
			List<KnowledgeDataType> childrenFolders = findChildren(topicTree, treeList);
			result.addAll(childrenFolders);
		});

		return result;
	}

}