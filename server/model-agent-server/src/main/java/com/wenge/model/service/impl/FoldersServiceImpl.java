package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Lists;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.FileDeleteParam;
import com.wenge.model.dto.param.FoldersDeleteParam;
import com.wenge.model.dto.param.FoldersPageParam;
import com.wenge.model.entity.Folders;
import com.wenge.model.enums.KnowledgeTypeEnum;
import com.wenge.model.mapper.FoldersMapper;
import com.wenge.model.service.FileService;
import com.wenge.model.service.FoldersService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.FoldersTableDef.FOLDERS;

/**
 * Description: 文件夹 服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 17:48:54
 *
 */
@Service
@Slf4j
public class FoldersServiceImpl extends ServiceImpl<FoldersMapper, Folders> implements FoldersService {
	/**
	 * 	文件夹 数据库处理类
	 */
	@Autowired
	private FoldersMapper foldersMapper;

	@Autowired
	private FileService fileService;

	@Override
	public Result<Folders> addFolders(Folders folders){
		if (StringUtils.isBlank(folders.getFoldersId())) {
			folders.setFoldersId(IdUtil.simpleUUID());
		}

		saveOrUpdate(folders);
		return Result.success(folders);
	}

	@Override
	public Folders createFolders(Folders folders){
		folders.setFoldersId(IdUtil.simpleUUID());
		save(folders);
		return folders;
	}

	@Override
	public Result<Page<Folders>> getFoldersList(FoldersPageParam folders){
		Wrappers<Object> wrapper = Wrappers.init()
				.where(StringUtils.isNotBlank(folders.getFolderName()), FOLDERS.NAME.like(folders.getFolderName()))
				.and(StringUtils.isNotBlank(folders.getKnowledgeId()), FOLDERS.KNOWLEDGE_ID.eq(folders.getKnowledgeId()))
				.and(StringUtils.isNotBlank(folders.getFoldersId()), FOLDERS.FOLDERS_ID.eq(folders.getFoldersId()))
				.and(folders.getType() != null, FOLDERS.TYPE.eq(folders.getType()));
		Page<Folders> page = page(Page.of(folders.getPageNo(), folders.getPageSize()), wrapper);
		return Result.success(page);
	}

	@Override
	public Result updateFolders(Folders folders){
		updateById(folders);
		return Result.success();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result deleteFolders(FoldersDeleteParam param){
		if (CollectionUtil.isEmpty(param.getFoldersIdList())) {
			return Result.success();
		}
		List<Folders> allFolders = mapper.selectAll();
		Map<String, List<Folders>> parentIdGroup = allFolders.stream().collect(Collectors.groupingBy(Folders::getParentId));

		// 获取文件夹id对知识库id的映射
		Map<String, String> folderIdToKnowledgeIdMap = allFolders.stream().collect(Collectors.toMap(
				Folders::getFoldersId,
				Folders::getKnowledgeId,
				(k1, k2) -> k1
		));

		// 删除文件
		for (String foldersId : param.getFoldersIdList()) {
			recursionDelete(parentIdGroup, foldersId, folderIdToKnowledgeIdMap.get(foldersId));
		}
		return Result.success();
	}

	@Override
	public Folders getByFoldersId(String foldersId) {
		return getOne(Wrappers.init().where(FOLDERS.FOLDERS_ID.eq(foldersId)));
	}

	@Override
	public Folders getByDesignatedFoldersName(String name, String knowledgeId, String parentId) {
		if (StringUtils.isBlank(name) || StringUtils.isBlank(knowledgeId) || StringUtils.isBlank(parentId)) {
			return null;
		}
		Folders one = Wrappers.of(mapper)
				.where(FOLDERS.NAME.eq(name))
				.and(FOLDERS.TYPE.eq(KnowledgeTypeEnum.DOCUMENT.getCode()))
				.and(FOLDERS.KNOWLEDGE_ID.eq(knowledgeId))
				.and(FOLDERS.PARENT_ID.eq(parentId))
				.limit(1)
				.one();
		return one;
	}

	@Override
	public Folders getByFoldersName(String foldersName, String knowledgeId) {
		if (StringUtils.isBlank(foldersName) || StringUtils.isBlank(knowledgeId)) {
			return null;
		}
		Folders one = Wrappers.of(mapper)
				.where(FOLDERS.NAME.eq(foldersName))
				.and(FOLDERS.TYPE.eq(KnowledgeTypeEnum.DOCUMENT.getCode()))
				.and(FOLDERS.KNOWLEDGE_ID.eq(knowledgeId))
				.limit(1)
				.one();
		return one;
	}

	@Override
	public List<Folders> getListByFoldersName(String foldersName, List<String> knowledgeIds) {
		if (StringUtils.isBlank(foldersName) || CollectionUtil.isEmpty(knowledgeIds)) {
			return null;
		}
		List<Folders> folders = Wrappers.of(mapper)
				.where(FOLDERS.NAME.eq(foldersName))
				.and(FOLDERS.TYPE.eq(KnowledgeTypeEnum.DOCUMENT.getCode()))
				.and(FOLDERS.KNOWLEDGE_ID.in(knowledgeIds))
				.list();
		return folders;
	}

	@Override
	public List<Folders> getFoldersForYayiDoc(List<String> knowledgeIds) {
		if (CollectionUtil.isEmpty(knowledgeIds)) {
			return Lists.newArrayList();
		}

        return Wrappers.of(mapper)
                .where(FOLDERS.KNOWLEDGE_ID.in(knowledgeIds))
                .and(FOLDERS.TYPE.in(KnowledgeTypeEnum.YAYI_KNOWLEDGE.getCode()))
                .list();
	}

	@Override
	public List<Folders> getKnowledgeIdsByFoldersId(List<String> foldersIds) {
		if (CollectionUtil.isEmpty(foldersIds)) {
			return Lists.newArrayList();
		}

		return Wrappers.of(mapper)
				.select(FOLDERS.KNOWLEDGE_ID, FOLDERS.FOLDERS_ID)
				.where(FOLDERS.FOLDERS_ID.in(foldersIds))
				.list();

	}


	@Override
	public List<Folders> getTreeByFolderId(String folderId, List<Folders> allFolders) {
		if (CollectionUtil.isEmpty(allFolders) || StringUtils.isBlank(folderId)) {
			return new ArrayList<>();
		}
		Optional<Folders> any = allFolders.stream().filter(v -> StringUtils.equals(v.getFoldersId(), folderId)).findAny();
		if (!any.isPresent()) {
			throw new RuntimeException("获取当前文件夹失败");
		}
		// 获取当前文件夹
		Folders currentFolders = any.get();

		return findChildren(currentFolders, allFolders);
	}

	@Override
	public String addFoldersByName(String foldersName, String knowledgeId) {
		if (StringUtils.isBlank(foldersName) || StringUtils.isBlank(knowledgeId)) {
			return StringConstant.BLANK;
		}
		Folders one = Wrappers.of(mapper)
				.where(FOLDERS.NAME.eq(foldersName))
				.and(FOLDERS.KNOWLEDGE_ID.eq(knowledgeId))
				.limit(1)
				.one();

		if (null != one) {
			return one.getFoldersId();
		}

		Folders creat = Folders.creat();
		creat.setFoldersId(IdUtil.simpleUUID());
		creat.setName(foldersName);
		creat.setKnowledgeId(knowledgeId);
		creat.setType(KnowledgeTypeEnum.DOCUMENT.getCode());
		creat.setCreateUser("workflow");

		creat.save();
		return creat.getFoldersId();
	}

	/**
	 * 递归删除文件夹
	 * @param parentIdGroup
	 * @param parentId
	 * @param knowledgeId
	 */
	private void recursionDelete(Map<String, List<Folders>> parentIdGroup, String parentId, String knowledgeId) {
		if (StringUtils.isBlank(parentId)) {
			return;
		}

		// 获取文件夹下所有子级文件夹
		List<Folders> childFolders = parentIdGroup.getOrDefault(parentId, Lists.newArrayList());
		// 获取文件夹id列表
		List<String> foldersIdList = childFolders.stream().map(Folders::getFoldersId).distinct().collect(Collectors.toList());
		foldersIdList.add(parentId);
		FileDeleteParam param = new FileDeleteParam();
		param.setFoldersIdList(foldersIdList);
		param.setKnowledgeId(knowledgeId);
		// 先删除文件
		fileService.deleteFile(param);

		// 删除文件夹，删除当前文件和子级文件夹
		Wrappers<Object> wrappers = Wrappers.init()
				.where(FOLDERS.PARENT_ID.eq(parentId))
				.or(FOLDERS.FOLDERS_ID.eq(parentId));
		mapper.deleteByQuery(wrappers);

		// 递归删除子级文件夹
		if (CollectionUtil.isNotEmpty(childFolders)) {
			for (Folders folder : childFolders) {
				recursionDelete(parentIdGroup, folder.getFoldersId(), knowledgeId);
			}
		}
	}



	/**
	 * 递归获取当前文件夹下所有文件夹
	 * @param folders
	 * @param treeList
	 * @return
	 */
	private List<Folders> findChildren(Folders folders, List<Folders> treeList) {
		List<Folders> result = new ArrayList<>();
		// 添加当前文件夹
		result.add(folders);
		// 获取当前文件夹下所有文件夹
		List<Folders> children = treeList.stream().filter(topicTree -> StringUtils.equals(topicTree.getParentId(), folders.getFoldersId())).collect(Collectors.toList());
		children.forEach(topicTree -> {
			// 递归获取当前文件夹下所有文件夹
			List<Folders> childrenFolders = findChildren(topicTree, treeList);
			result.addAll(childrenFolders);
		});

		return result;
	}

}