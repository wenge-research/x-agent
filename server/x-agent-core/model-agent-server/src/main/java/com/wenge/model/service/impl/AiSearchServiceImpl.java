package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.dto.param.AiSearchRequest;
import com.wenge.model.dto.param.FileDataPageParam;
import com.wenge.model.dto.param.FileResult;
import com.wenge.model.entity.*;
import com.wenge.model.enums.ApplicationKnowledgeTypeEnum;
import com.wenge.model.mapper.ApplicationInfoMapper;
import com.wenge.model.service.*;
import com.wenge.model.utils.AnswerUtils;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import jodd.util.StringUtil;
import org.apache.commons.compress.utils.Lists;
import org.dromara.easyes.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.AiSearchDataAnalysisTableDef.AI_SEARCH_DATA_ANALYSIS;
import static com.wenge.model.entity.table.AiSearchFileSubHistoryTableDef.AI_SEARCH_FILE_SUB_HISTORY;
import static com.wenge.model.entity.table.ApplicationInfoTableDef.APPLICATION_INFO;
import static com.wenge.model.entity.table.FileTableDef.FILE;
import static com.wenge.model.entity.table.FoldersTableDef.FOLDERS;

@Service
public class AiSearchServiceImpl implements AiSearchService {

    @Autowired
    private ApplicationInfoMapper applicationInfoMapper;

    @Autowired
    private AnswerUtils answerUtils;

    @Autowired
    private ApplicationKnowledgeService applicationKnowledgeService;

    @Autowired
    private FileService fileService;

    @Autowired
    private AiSearchFileSubHistoryService aiSearchFileSubHistoryService;

    @Value("${aiCommand.jghAnswer}")
    private String jghAnswer;

    @Value("${aiCommand.generateRequest}")
    private String generateRequest;

    @Autowired
    private FileDataService fileDataService;

    @Override
    public Result searchByWord(AiSearchRequest aiSearchRequest) {

        QueryWrapper wrapper = Wrappers.init()
                .and(StringUtils.isNotEmpty(aiSearchRequest.getApplicationId()),
                        APPLICATION_INFO.APPLICATION_ID.eq(aiSearchRequest.getApplicationId()));
        List<ApplicationInfo> listApp= applicationInfoMapper.selectListByQuery(wrapper);
        ApplicationInfo applicationInfo = new ApplicationInfo();
        if(CollectionUtils.isNotEmpty(listApp)&& !listApp.isEmpty()){
            applicationInfo= listApp.get(0);
        }
        String generateRequest = this.generateRequest;
        generateRequest = generateRequest.replace("{applicationName}", applicationInfo.getApplicationName());
        generateRequest = generateRequest.replace("{introduce}", applicationInfo.getIntroduce());
        generateRequest = generateRequest.replace("{keyWord}", aiSearchRequest.getKeyWord());
        generateRequest = generateRequest.replace("{questionNum}", aiSearchRequest.getQuestionNum());

        DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
        String generateCommon = answerUtils.getGenerateCommon(null,
                generateRequest,
                new StepEntity(), null, null);
        int startIndex1 = generateCommon.indexOf("[");
        int endIndex1 = generateCommon.lastIndexOf("]");
        if (startIndex1 != -1&&endIndex1!= -1) {
            generateCommon= generateCommon.substring(startIndex1, endIndex1+1);
        } else {
            return Result.fail("大模式出错");
        }
        return Result.success(JSONUtil.parseArray(generateCommon));
    }

    @Override
    public Result searchByJgh(AiSearchRequest aiSearchRequest) {
//        if (StringUtil.isBlank(aiSearchRequest.getApplicationId())) {
//            return Result.fail("应用id不能为空");
//        }
        if (StringUtil.isBlank(aiSearchRequest.getQuestion())) {
            return Result.fail("question不能为空");
        }
        QueryWrapper wrapper = Wrappers.init()
                .and(StringUtils.isNotEmpty(aiSearchRequest.getApplicationId()),
                        APPLICATION_INFO.APPLICATION_ID.eq(aiSearchRequest.getApplicationId()));
        List<ApplicationInfo> listApp= applicationInfoMapper.selectListByQuery(wrapper);
        ApplicationInfo applicationInfo = new ApplicationInfo();
        if(CollectionUtils.isNotEmpty(listApp)&& !listApp.isEmpty()){
            applicationInfo= listApp.get(0);
        }
        String jghAnswer = this.jghAnswer;
        jghAnswer= jghAnswer.replace("{question}", aiSearchRequest.getQuestion());
        DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
        String generateCommon = answerUtils.getGenerateCommon(null,
                jghAnswer,
                new StepEntity(), null, null);
        int startIndex1 = generateCommon.indexOf("<table>");
        int endIndex1 = generateCommon.lastIndexOf("</table>");
        if (startIndex1 != -1&&endIndex1!= -1) {
            generateCommon= generateCommon.substring(startIndex1, endIndex1+8).replaceAll("\\n", "");
        } else {
            return Result.fail("大模式出错");
        }
        return Result.success(generateCommon);
    }

    @Override
    public Result queryKnowledgeList(AiSearchRequest aiSearchRequest) {
        if (StringUtil.isBlank(aiSearchRequest.getApplicationId())) {
            return Result.fail("应用id不能为空");
        }
        List<ApplicationKnowledge> applicationKnowledges = applicationKnowledgeService.list();
        Map<String, List<ApplicationKnowledge>> knowledgeMap = applicationKnowledges.stream()
                .filter(p -> p.getType().equals(ApplicationKnowledgeTypeEnum.KNOWLEDGE.getType()))
                .collect(Collectors.groupingBy(ApplicationKnowledge::getApplicationId, Collectors.collectingAndThen(Collectors.toList(), listData -> listData)));
        List<ApplicationKnowledge> knowledge = knowledgeMap.getOrDefault(aiSearchRequest.getApplicationId(), Lists.newArrayList());
        return Result.success(knowledge);
    }

    @Override
    public Result<Page<File>> getFileList(AiSearchRequest aiSearchRequest) {
        if (StringUtil.isBlank(aiSearchRequest.getApplicationId())) {
            return Result.fail("应用id不能为空");
        }
        if (StringUtil.isBlank(aiSearchRequest.getQuestion())) {
            return Result.fail("question不能为空");
        }


        List<ApplicationKnowledge> knowledgeList = (List<ApplicationKnowledge>) queryKnowledgeList(aiSearchRequest).getData();
        List<String> knowledgeIds = knowledgeList.stream().map(ApplicationKnowledge::getKnowledgeId).collect(Collectors.toList());
        // 需要先根据文件内容查询出包含关键字的文档
        FileDataPageParam param = new FileDataPageParam();
        param.setContent(aiSearchRequest.getQuestion());
        param.setKnowledgeId(aiSearchRequest.getKnowledgeId());
        param.setKnowledgeIds(knowledgeIds);
        param.setPageNo(aiSearchRequest.getPageNo());
        param.setPageSize(aiSearchRequest.getPageSize());
        List<FileData> fileDataList = fileDataService.getFileDataKnowledges(param);
        List<String> fileIds =  fileDataList.stream().map(FileData::getFileId).distinct().collect(Collectors.toList());
        // 这里添加-1，是为了防止查询到空数据
        fileIds.add("-1");
        List<String> foldersIds = Lists.newArrayList();
        // 有文件夹才可以导入文件
        List<Folders> allFolders = Folders.creat()
                .where(FOLDERS.KNOWLEDGE_ID.in(knowledgeIds))
                .and(StringUtil.isNotEmpty(aiSearchRequest.getKnowledgeId()), FOLDERS.KNOWLEDGE_ID.eq(aiSearchRequest.getKnowledgeId()))
                .lists();
        // 这里添加-1的目录，是为了防止查询到非当前知识库的文件
        foldersIds.add("-1");
        List<String> foldersIdList = allFolders.stream().map(Folders::getFoldersId).collect(Collectors.toList());
        foldersIds.addAll(foldersIdList);
        // 如果有传入文件id，则检索该文件夹下的文件，如果没有文件夹id，则查询该知识库下所有文件夹的文件
        Wrappers<Object> wrapper = Wrappers.init()
                .select(FILE.ALL_COLUMNS, AI_SEARCH_DATA_ANALYSIS.LIKE_NUM, AI_SEARCH_DATA_ANALYSIS.READ_NUM, AI_SEARCH_DATA_ANALYSIS.SUB_NUM)
                .where(CollectionUtil.isNotEmpty(foldersIds), FILE.FOLDERS_ID.in(foldersIds))
                .leftJoin(AI_SEARCH_DATA_ANALYSIS).on(FILE.FILE_ID.eq(AI_SEARCH_DATA_ANALYSIS.FILE_ID))
                .and(CollectionUtil.isNotEmpty(fileIds), FILE.FILE_ID.in(fileIds))
                .and(CollectionUtil.isNotEmpty(aiSearchRequest.getTypes()),
                        FILE.TYPE.in(aiSearchRequest.getTypes()))
                .and(CollectionUtil.isNotEmpty(aiSearchRequest.getFileTypes()),
                        FILE.FILE_TYPE.in(aiSearchRequest.getFileTypes()))
                .and(StringUtils.isNotBlank(aiSearchRequest.getCreateStartTime()), FILE.CREATE_TIME.ge(aiSearchRequest.getCreateStartTime()))
                .and(StringUtils.isNotBlank(aiSearchRequest.getCreateEndTime()), FILE.CREATE_TIME.le(aiSearchRequest.getCreateEndTime()))
                .and(aiSearchRequest.getFileSizeMin() != null, FILE.FILE_SIZE.ge(aiSearchRequest.getFileSizeMin()))
                .and(aiSearchRequest.getFileSizeMax() != null, FILE.FILE_SIZE.le(aiSearchRequest.getFileSizeMax()));
        if ("0".equals(aiSearchRequest.getIsDesc())) {
            wrapper.orderBy(FILE.CREATE_TIME.desc());
        } else {
            wrapper.orderBy(FILE.CREATE_TIME.asc());
        }

        Page<File> page = fileService.page(Page.of(aiSearchRequest.getPageNo(), aiSearchRequest.getPageSize()), wrapper);
        // 计算平均段落长度
        List<File> records = page.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {
            records.forEach(v->{
                if (!Objects.isNull(v.getWordCount()) && !Objects.isNull(v.getParagraphsNum())) {
                    v.setAverageParagraphLength(v.getWordCount() / v.getParagraphsNum());
                } else {
                    v.setAverageParagraphLength(0);
                }
            });
        }
        return Result.success(page);
    }

    @Override
    public Result<Page<FileResult>> getHotFileList(AiSearchRequest aiSearchRequest) {
        if (StringUtil.isBlank(aiSearchRequest.getApplicationId())) {
            return Result.fail("应用id不能为空");
        }
        List<ApplicationKnowledge> knowledgeList = (List<ApplicationKnowledge>) queryKnowledgeList(aiSearchRequest).getData();
        List<String> knowledgeIds = knowledgeList.stream().map(ApplicationKnowledge::getKnowledgeId).collect(Collectors.toList());
        List<String> foldersIds = Lists.newArrayList();


        TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
        // 查询我订阅的文档列表
        QueryWrapper wrappers = Wrappers.init()
                .select(AI_SEARCH_FILE_SUB_HISTORY.FILE_ID)
                .where(AI_SEARCH_FILE_SUB_HISTORY.APPLICATION_ID.eq(aiSearchRequest.getApplicationId()))
                .and(AI_SEARCH_FILE_SUB_HISTORY.USER_ID.eq(tokenOauthUserInfo.getId()));
        List<String> fileLists = aiSearchFileSubHistoryService.listAs(wrappers, String.class);
        // 有文件夹才可以导入文件
        List<Folders> allFolders = Folders.creat()
                .where(FOLDERS.KNOWLEDGE_ID.in(knowledgeIds))
                .and(StringUtil.isNotEmpty(aiSearchRequest.getKnowledgeId()), FOLDERS.KNOWLEDGE_ID.eq(knowledgeIds))
                .lists();
        // 这里添加-1的目录，是为了防止查询到非当前知识库的文件
        foldersIds.add("-1");
        List<String> foldersIdList = allFolders.stream().map(Folders::getFoldersId).collect(Collectors.toList());
        foldersIds.addAll(foldersIdList);
        // 如果有传入文件id，则检索该文件夹下的文件，如果没有文件夹id，则查询该知识库下所有文件夹的文件
        Wrappers<Object> wrapper = Wrappers.init()
                .select(FILE.ALL_COLUMNS, AI_SEARCH_DATA_ANALYSIS.LIKE_NUM, AI_SEARCH_DATA_ANALYSIS.READ_NUM, AI_SEARCH_DATA_ANALYSIS.SUB_NUM)
                .where(CollectionUtil.isNotEmpty(foldersIds), FILE.FOLDERS_ID.in(foldersIds))
                .leftJoin(AI_SEARCH_DATA_ANALYSIS).on(FILE.FILE_ID.eq(AI_SEARCH_DATA_ANALYSIS.FILE_ID))
                .and(FILE.FILE_NAME.like(aiSearchRequest.getQuestion()))
                .and(CollectionUtil.isNotEmpty(aiSearchRequest.getTypes()),
                        FILE.TYPE.in(aiSearchRequest.getTypes()))
                .and(CollectionUtil.isNotEmpty(aiSearchRequest.getFileTypes()),
                        FILE.FILE_TYPE.in(aiSearchRequest.getFileTypes()))
                .orderBy(AI_SEARCH_DATA_ANALYSIS.LIKE_NUM.desc(), FILE.CREATE_TIME.desc());
        Page<FileResult> page = fileService.pageAs(Page.of(aiSearchRequest.getPageNo(), aiSearchRequest.getPageSize()), wrapper, FileResult.class);
        // 计算平均段落长度
        List<FileResult> records = page.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {
            records.forEach(v->{
                if (fileLists.contains(v.getFileId())) {
                    v.setIsSub("1");
                } else {
                    v.setIsSub("0");
                }
                if (Objects.isNull(v.getSubNum())) {
                    v.setSubNum(0);
                }
                if (Objects.isNull(v.getLikeNum())) {
                    v.setLikeNum(0);
                }
                if (Objects.isNull(v.getReadNum())) {
                    v.setReadNum(0);
                }
                if (!Objects.isNull(v.getWordCount()) && !Objects.isNull(v.getParagraphsNum())) {
                    v.setAverageParagraphLength(v.getWordCount() / v.getParagraphsNum());
                } else {
                    v.setAverageParagraphLength(0);
                }
            });
        }
        return Result.success(page);
    }


    @Override
    public Result<Page<FileResult>> getSubFileList(AiSearchRequest aiSearchRequest) {
        if (StringUtil.isBlank(aiSearchRequest.getApplicationId())) {
            return Result.fail("应用id不能为空");
        }
        List<ApplicationKnowledge> knowledgeList = (List<ApplicationKnowledge>) queryKnowledgeList(aiSearchRequest).getData();
        List<String> knowledgeIds = knowledgeList.stream().map(ApplicationKnowledge::getKnowledgeId).collect(Collectors.toList());

        TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
        // 查询我订阅的文档列表
        QueryWrapper wrappers = Wrappers.init()
                .select(AI_SEARCH_FILE_SUB_HISTORY.FILE_ID)
                .where(AI_SEARCH_FILE_SUB_HISTORY.APPLICATION_ID.eq(aiSearchRequest.getApplicationId()))
                .and(AI_SEARCH_FILE_SUB_HISTORY.USER_ID.eq(tokenOauthUserInfo.getId()));
        List<String> fileLists = aiSearchFileSubHistoryService.listAs(wrappers, String.class);

        List<String> foldersIds = Lists.newArrayList();
        // 有文件夹才可以导入文件
        List<Folders> allFolders = Folders.creat()
                .where(FOLDERS.KNOWLEDGE_ID.in(knowledgeIds))
                .and(StringUtil.isNotEmpty(aiSearchRequest.getKnowledgeId()), FOLDERS.KNOWLEDGE_ID.eq(knowledgeIds))
                .lists();
        // 这里添加-1的目录，是为了防止查询到非当前知识库的文件
        foldersIds.add("-1");
        List<String> foldersIdList = allFolders.stream().map(Folders::getFoldersId).collect(Collectors.toList());
        foldersIds.addAll(foldersIdList);
        // 如果有传入文件id，则检索该文件夹下的文件，如果没有文件夹id，则查询该知识库下所有文件夹的文件
        Wrappers<Object> wrapper = Wrappers.init()
                .select(FILE.ALL_COLUMNS, AI_SEARCH_DATA_ANALYSIS.LIKE_NUM, AI_SEARCH_DATA_ANALYSIS.READ_NUM, AI_SEARCH_DATA_ANALYSIS.SUB_NUM)
                .where(CollectionUtil.isNotEmpty(foldersIds), FILE.FOLDERS_ID.in(foldersIds))
                .leftJoin(AI_SEARCH_DATA_ANALYSIS).on(FILE.FILE_ID.eq(AI_SEARCH_DATA_ANALYSIS.FILE_ID))
                .and(FILE.FILE_NAME.like(aiSearchRequest.getQuestion()))
                .and(CollectionUtil.isNotEmpty(aiSearchRequest.getTypes()),
                        FILE.TYPE.in(aiSearchRequest.getTypes()))
                .and(CollectionUtil.isNotEmpty(aiSearchRequest.getFileTypes()),
                        FILE.FILE_TYPE.in(aiSearchRequest.getFileTypes()))
                .orderBy(AI_SEARCH_DATA_ANALYSIS.SUB_NUM.desc(), FILE.CREATE_TIME.desc());
        Page<FileResult> page = fileService.pageAs(Page.of(aiSearchRequest.getPageNo(), aiSearchRequest.getPageSize()), wrapper, FileResult.class);
        // 计算平均段落长度
        List<FileResult> records = page.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {
            records.forEach(v->{
                if (fileLists.contains(v.getFileId())) {
                    v.setIsSub("1");
                } else {
                    v.setIsSub("0");
                }
                if (Objects.isNull(v.getSubNum())) {
                    v.setSubNum(0);
                }
                if (Objects.isNull(v.getLikeNum())) {
                    v.setLikeNum(0);
                }
                if (Objects.isNull(v.getReadNum())) {
                    v.setReadNum(0);
                }
                if (!Objects.isNull(v.getWordCount()) && !Objects.isNull(v.getParagraphsNum())) {
                    v.setAverageParagraphLength(v.getWordCount() / v.getParagraphsNum());
                } else {
                    v.setAverageParagraphLength(0);
                }
            });
        }
        return Result.success(page);
    }

    @Override
    public Result<Page<FileResult>> getMySubFileList(AiSearchRequest aiSearchRequest) {
        if (StringUtil.isBlank(aiSearchRequest.getApplicationId())) {
            return Result.fail("应用id不能为空");
        }
        List<ApplicationKnowledge> knowledgeList = (List<ApplicationKnowledge>) queryKnowledgeList(aiSearchRequest).getData();
        List<String> knowledgeIds = knowledgeList.stream().map(ApplicationKnowledge::getKnowledgeId).collect(Collectors.toList());
        List<String> foldersIds = Lists.newArrayList();

        TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
        // 查询我订阅的文档列表
        QueryWrapper wrappers = Wrappers.init()
                .select(AI_SEARCH_FILE_SUB_HISTORY.FILE_ID)
                .where(AI_SEARCH_FILE_SUB_HISTORY.APPLICATION_ID.eq(aiSearchRequest.getApplicationId()))
                .and(AI_SEARCH_FILE_SUB_HISTORY.USER_ID.eq(tokenOauthUserInfo.getId()));
        List<String> fileLists = aiSearchFileSubHistoryService.listAs(wrappers, String.class);

        // 有文件夹才可以导入文件
        List<Folders> allFolders = Folders.creat()
                .where(FOLDERS.KNOWLEDGE_ID.in(knowledgeIds))
                .and(StringUtil.isNotEmpty(aiSearchRequest.getKnowledgeId()), FOLDERS.KNOWLEDGE_ID.eq(knowledgeIds))
                .lists();
        // 这里添加-1的目录，是为了防止查询到非当前知识库的文件
        foldersIds.add("-1");
        List<String> foldersIdList = allFolders.stream().map(Folders::getFoldersId).collect(Collectors.toList());
        foldersIds.addAll(foldersIdList);
        // 如果有传入文件id，则检索该文件夹下的文件，如果没有文件夹id，则查询该知识库下所有文件夹的文件
        Wrappers<Object> wrapper = Wrappers.init()
                .select(FILE.ALL_COLUMNS, AI_SEARCH_DATA_ANALYSIS.LIKE_NUM, AI_SEARCH_DATA_ANALYSIS.READ_NUM, AI_SEARCH_DATA_ANALYSIS.SUB_NUM)
                .where(CollectionUtil.isNotEmpty(foldersIds), FILE.FOLDERS_ID.in(foldersIds))
                .leftJoin(AI_SEARCH_DATA_ANALYSIS).on(FILE.FILE_ID.eq(AI_SEARCH_DATA_ANALYSIS.FILE_ID))
                .and(FILE.FILE_NAME.like(aiSearchRequest.getQuestion()))
                .and(FILE.FILE_ID.in(fileLists))
                .and(CollectionUtil.isNotEmpty(aiSearchRequest.getTypes()),
                        FILE.TYPE.in(aiSearchRequest.getTypes()))
                .and(CollectionUtil.isNotEmpty(aiSearchRequest.getFileTypes()),
                        FILE.FILE_TYPE.in(aiSearchRequest.getFileTypes()))
                .orderBy(AI_SEARCH_DATA_ANALYSIS.SUB_NUM.desc(), FILE.CREATE_TIME.desc());
        Page<FileResult> page = fileService.pageAs(Page.of(aiSearchRequest.getPageNo(), aiSearchRequest.getPageSize()), wrapper, FileResult.class);

        // 计算平均段落长度
        List<FileResult> records = page.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {
            records.forEach(v->{
                v.setIsSub("1");
                if (Objects.isNull(v.getSubNum())) {
                    v.setSubNum(0);
                }
                if (Objects.isNull(v.getLikeNum())) {
                    v.setLikeNum(0);
                }
                if (Objects.isNull(v.getReadNum())) {
                    v.setReadNum(0);
                }
                if (!Objects.isNull(v.getWordCount()) && !Objects.isNull(v.getParagraphsNum())) {
                    v.setAverageParagraphLength(v.getWordCount() / v.getParagraphsNum());
                } else {
                    v.setAverageParagraphLength(0);
                }
            });
        }
        return Result.success(page);
    }
}
