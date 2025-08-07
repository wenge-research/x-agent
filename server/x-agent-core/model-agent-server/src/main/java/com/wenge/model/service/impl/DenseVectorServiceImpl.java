package com.wenge.model.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Maps;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.DenseVectorPageParam;
import com.wenge.model.entity.DenseVector;
import com.wenge.model.entity.KnowledgeInfo;
import com.wenge.model.enums.DenseVectorEnableEnum;
import com.wenge.model.mapper.DenseVectorMapper;
import com.wenge.model.service.DenseVectorService;
import com.wenge.model.service.KnowledgeInfoService;
import com.wenge.model.strategy.embedding.EmbeddingStrategy;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.enums.PowerTypeEnum;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.DenseVectorTableDef.DENSE_VECTOR;

/**
 * Description: 向量化模型服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-09-11 14:45:53
 *
 */
@Service
@Slf4j
public class DenseVectorServiceImpl extends ServiceImpl<DenseVectorMapper, DenseVector> implements DenseVectorService {
	/**
	 * 	向量化模型数据库处理类
	 */
	@Autowired
	private DenseVectorMapper denseVectorMapper;

	@Autowired
	private KnowledgeInfoService knowledgeInfoService;

	@Autowired
	private Map<String, EmbeddingStrategy> embeddingStrategyMap;

	/**
	 * 	向量化模型缓存，这里添加一下锁
	 */
	public static final Map<String, DenseVector> DENSE_VECTOR_MAP = Maps.newHashMap();
	public static final Map<String, DenseVector> VECTOR_ID_MAP = Maps.newHashMap();

	/**
	 * 	初始化向量化模型缓存
	 */
	@PostConstruct
	public void init() {
		List<KnowledgeInfo> allKnowledgeInfo = knowledgeInfoService.getAllKnowledgeInfo();
		List<DenseVector> list = list();
		if (CollectionUtil.isNotEmpty(allKnowledgeInfo)) {
			Map<String, DenseVector> denseVectorMap = list.stream().collect(Collectors.toMap(
					DenseVector::getVectorId,
					p -> p,
					(k1, k2) -> k1
			));
			allKnowledgeInfo.forEach(knowledgeInfo -> DENSE_VECTOR_MAP.put(knowledgeInfo.getKnowledgeId(), denseVectorMap.get(knowledgeInfo.getDenseVectorId())));
			log.info("向量化模型缓存初始化完成，共{}个知识库", DENSE_VECTOR_MAP.size());

			Map<String, DenseVector> map = list.stream().collect(Collectors.toMap(
                    DenseVector::getVectorId,
					p -> p,
					(k1, k2) -> k1
			));
			VECTOR_ID_MAP.putAll(map);
		}
	}

	@Override
	public Result addDenseVector(DenseVector denseVector){
		// 用户信息
		TokenUser user = AppContextHolder.getTokenUserInfo();
		if (null != user) {
			denseVector.setCreateUser(user.getAccountName());
			denseVector.setUpdateUser(user.getAccountName());
			denseVector.setTenantId(user.getTenantId());
		}
		denseVector.setVectorId(IdUtil.simpleUUID());
		save(denseVector);
		return Result.success();
	}

	@Override
	public Result getDenseVectorList(DenseVectorPageParam denseVector) {
		/*
		 *向量化模型权限控制
		 * 1.系统超级管理员:可以查询全部向量化模型数据
		 * 2.租户管理员(普通管理员):可以查询当前租户下的所有向量化模型数据
		 * 3.普通用户:只能查看自己创建的向量化模型数据(由于普通用户无页面菜单权限,此处也会将查询条件加上,若是后期赋予普通用户菜单权限了,可以直接使用)
		 */
		// 获取用户信息
		TokenUser user = AppContextHolder.getTokenUserInfo();
		if (null == user) {
			return Result.fail("无法获取到登录用户信息");
		}
		if (StringUtils.isBlank(user.getPowerType())) {
			return Result.fail("用户权限类型为空,暂无权限");
		}

		// 只有管理员才能查看
		if (PowerTypeEnum.NORMAL_USER.getCode().equals(user.getPowerType()) || PowerTypeEnum.WECHAT_USER.getCode().equals(user.getPowerType())) {
			return Result.fail("用户权限类型为空,暂无权限");
		}

		Wrappers<DenseVector> wrappers = Wrappers.of(mapper)
				.where(StringUtils.isNotBlank(denseVector.getKeyword()), DENSE_VECTOR.VECTOR_NAME.like(denseVector.getKeyword()));

		/*
		 *用户数据权限控制
		 */
		// 普通用户
		// if (StringUtils.isNotBlank(user.getPowerType()) && PowerTypeEnum.NORMAL_USER.getCode().equals(user.getPowerType())) {
		// 	wrappers.and(DENSE_VECTOR.CREATE_USER.eq(user.getAccountName()));
		// }
		// // 普通管理员(租户管理员)
		// if (StringUtils.isNotBlank(user.getPowerType()) && PowerTypeEnum.NORMAL_ADMIN.getCode().equals(user.getPowerType())) {
		// 	wrappers.and(DENSE_VECTOR.TENANT_ID.eq(user.getTenantId()));
		// }

		Page<DenseVector> page = page(Page.of(denseVector.getPageNo(), denseVector.getPageSize()), wrappers);
		return Result.success(page);
	}

	@Override
	public Result updateDenseVector(DenseVector denseVector){
		// 用户信息
		TokenUser user = AppContextHolder.getTokenUserInfo();
		if (null != user) {
			denseVector.setUpdateUser(user.getAccountName());
		}
		updateById(denseVector);
		return Result.success();
	}

	@Override
	public Result deleteDenseVector(List<String> idList){
		removeByIds(idList);
		return Result.success();
	}

	@Override
	public DenseVector getDenseVectorById(String vectorId) {
		if (StringUtils.isBlank(vectorId)) {
			return null;
		}
		List<DenseVector> denseVectorByIds = getDenseVectorByIds(ListUtil.toList(vectorId));
		if (CollectionUtils.isNotEmpty(denseVectorByIds)) {
			return denseVectorByIds.get(0);
		}
		return null;
	}

	@Override
	public List<DenseVector> getDenseVectorByIds(List<String> vectorId) {
		if (CollectionUtils.isEmpty(vectorId)) {
			return Lists.newArrayList();
		}
		return Wrappers.of(mapper)
				.where(DENSE_VECTOR.VECTOR_ID.in(vectorId))
				.list();
	}

	@Override
	public DenseVector getDenseVectorCode(String vectorCode) {
		if (StringUtils.isBlank(vectorCode)) {
			return null;
		}
		return Wrappers.of(mapper)
				.where(DENSE_VECTOR.VECTOR_CODE.eq(vectorCode))
				.one();
	}

	@Override
	public List<Double> modelEncode(String query, String knowledgeId, Object data, String field) {
		if (StringUtils.isBlank(query)) {
			return Lists.newArrayList();
		}

		// 初始化向量化模型
		if (DENSE_VECTOR_MAP.isEmpty()) {
			init();
		}

		DenseVector denseVector = DENSE_VECTOR_MAP.get(knowledgeId);

		// 获取向量化模型
		if (null == denseVector) {
			init();
			denseVector = DENSE_VECTOR_MAP.get(knowledgeId);
			if (null == denseVector) {
				log.warn("向量化模型不存在，knowledgeId:{}", knowledgeId);
				return Lists.newArrayList();
			}
		}

		EmbeddingStrategy embeddingStrategy = embeddingStrategyMap.get(denseVector.getVectorCode());
		if (null == embeddingStrategy) {
			log.warn("向量化模型不存在，vectorCode:{}", denseVector.getVectorCode());
			return Lists.newArrayList();
		}

		// 获取向量化结果
		List<Double> embedding = embeddingStrategy.embedding(query);

		// 存储向量化结果
		if (CollectionUtil.isNotEmpty(embedding) && null != data && StringUtils.isNotBlank(field)) {
			if (embedding.size() == 768) {
				try {
					Field f = data.getClass().getDeclaredField(field);
					Method setter = data.getClass().getMethod("set" + capitalize(field), f.getType());
					setter.invoke(data, embedding);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (embedding.size() == 1024) {
				try {
					Field f = data.getClass().getDeclaredField(field + "1024");
					Method setter = data.getClass().getMethod("set" + capitalize(field + "1024"), f.getType());
					setter.invoke(data, embedding);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return embedding;
	}

	@Override
	public List<Double> modelEncode(String query, String knowledgeId) {
		return modelEncode(query, knowledgeId, null, null);
	}

	@Override
	public List<DenseVector> getAllDenseVector() {
		return list();
	}

	@Override
	public DenseVector getOneDefaultDenseVector() {
		List<DenseVector> list = Wrappers.of(mapper)
				.where(DENSE_VECTOR.ENABLE_FLAG.eq(DenseVectorEnableEnum.ENABLE.getCode()))
				.list();
		if (CollectionUtil.isEmpty(list)) {
			return null;
		}

		return list.get(0);
	}

	/**
	 * 辅助方法用于首字母大写
	 * @param s
	 * @return
	 */
	private static String capitalize(String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}
}