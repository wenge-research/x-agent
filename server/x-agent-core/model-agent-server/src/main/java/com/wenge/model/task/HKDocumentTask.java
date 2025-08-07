package com.wenge.model.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.net.NetUtil;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.update.UpdateChain;
import com.wenge.model.constants.FileDataTypeConstants;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.entity.File;
import com.wenge.model.entity.FileData;
import com.wenge.model.enums.FileStatusEnum;
import com.wenge.model.enums.FileTypeEnum;
import com.wenge.model.mapper.es.FileDataMapper;
import com.wenge.model.service.DenseVectorService;
import com.wenge.model.service.FileService;
import com.wenge.model.vo.ParserDocumentVo;
import com.wenge.oauth.constants.RedisConstant;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.wenge.model.constants.AnswerStrategyContant.CONTENT_DENSE_FILED;
import static com.wenge.model.entity.table.FileTableDef.FILE;
import static com.wenge.model.entity.table.FoldersTableDef.FOLDERS;
import static com.wenge.model.entity.table.KnowledgeInfoTableDef.KNOWLEDGE_INFO;

/**
 * 香港文档解析任务
 */
@RestController
@RequestMapping("/agentTask/hkDocument")
@Slf4j
public class HKDocumentTask {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileDataMapper fileDataMapper;

    @Value("${task.hkDocument.enable}")
    private boolean enable;

    @Autowired
    private DenseVectorService denseVectorService;

    @Autowired
    private RedisService redisService;

    @Value("${task.hkDocument.knowledgeId}")
    private String knowledgeId;

    @Value("${task.hkDocument.apiUrl}")
    private String apiUrl;

    @PostConstruct
    public void init() {

        String value = redisService.get(RedisKey.HK_PARSE_DOC, String.class);
        String localhostStr = NetUtil.getLocalhostStr();

        if (StringUtils.isNotBlank(value) && value.startsWith(localhostStr)) {
            redisService.del(RedisKey.HK_PARSE_DOC);
            redisService.unlock(RedisConstant.LOCK_HK_PARSE_DOC);
        }
    }

    /**
     * 解析文档
     */
    @GetMapping("/hkDocumentAnalysis")
    // @Scheduled(cron = "${task.hkDocument.cron}")
    public void documentAnalysis() {
        long start01 = System.currentTimeMillis();
        log.info("==>解析文档定时器开始执行==== 时间毫秒： {}", start01);
        long start02 = 0L;
        if (!enable) {
            start02 = System.currentTimeMillis();
            log.info("==>解析文档定时器 状态关闭==== 耗时毫秒： {}", (start02 - start01));
            return;
        }

        boolean lock = redisService.lock(RedisConstant.LOCK_HK_PARSE_DOC);
        long start03 = 0L;
        if (!lock) {
            log.info("==>任务未获取到锁，跳过当前任务");
            start03 = System.currentTimeMillis();
            log.info("==>解析文档定时器 未获取到锁==== 耗时毫秒： {}", (start03 - start02));
            return;
        }

        String redisKey = RedisKey.HK_PARSE_DOC;
        long start04 = 0L;
        if (redisService.hasKey(redisKey)) {
            log.info("定时器正在执行，本次跳过");
            start04 = System.currentTimeMillis();
            log.info("==>解析文档定时器 redis-key被占用，跳过==== 耗时毫秒： {}", (start04 - start03));
            return;
        }
        long start05 = System.currentTimeMillis();
        log.info("==>解析文档定时器 开始执行解析逻辑==== 耗时毫秒： {}", (start05 - start04));
        String localhostStr = NetUtil.getLocalhostStr();
        String currentDateString = DateUtil.getCurrentDateString();
        redisService.set(redisKey, localhostStr + "_" + currentDateString, 60 * 60 * 24 * 7);

        long start06 = System.currentTimeMillis();
        log.info("==>解析文档定时器 开始查询解析详情==== 耗时毫秒： {}", (start06 - start05));
        try {
            // 获取所有未成功解析的文件
            List<File> fileList = getFileList();
            if (CollectionUtil.isEmpty(fileList)) {
                log.info("==>解析文档定时器 获取所有未成功解析的文件==== {}", ("没有需要解析的文档"));
                return;
            }
            parseFile(fileList);
        } catch (Exception e) {
            log.error("解析文档定时器异常,error:{}", e.getMessage(), e);
        } finally {
            redisService.del(redisKey);
            redisService.unlock(RedisConstant.LOCK_HK_PARSE_DOC);
        }
        long start07 = System.currentTimeMillis();
        log.info("==>解析文档定时器 开始查询解析详情==== 耗时毫秒： {}", (start07 - start06));
        log.info("==>解析文档定时器 本次执行总耗时==== 总耗时毫秒： {}", (start07 - start01));
    }

    /**
     * 解析文件
     * @param fileList
     */
    private void parseFile(List<File> fileList) {

        fileList.forEach(file -> {
            // 更新状态，解析中
            updateStatus(file);
            JSONArray jsonArray = new JSONArray();
            JSONObject param = new JSONObject();
            param.set("file_url", file.getFileUrl());
            param.set("file_name", file.getFileName());
            jsonArray.add(param);
            String jsonStr =jsonArray.toString();
            String post = HttpUtil.post(apiUrl, jsonStr);
            log.info("解析文件返回结果：{}", post);
            if (StringUtils.isNotBlank(post)) {
                JSONObject entries = JSONUtil.parseObj(post);
                if (entries.containsKey("code") && entries.getStr("code") != null) {
                    int code = Integer.parseInt(entries.get("code").toString());
                    if (code == 200) {
                        String currentDateString = DateUtil.getCurrentDateString();
                        JSONArray data = entries.getJSONArray("data");
                        if (CollectionUtil.isNotEmpty(data)) {
                            JSONObject firstObj = data.getJSONObject(0);
                            ParserDocumentVo parserDocumentVo = JSONUtil.toBean(firstObj.toString(), ParserDocumentVo.class);
                            List<FileData> fileDataList = new ArrayList<>();
                            parserDocumentVo.getFile_text().forEach(item -> {
                                FileData fileData = new FileData();
                                fileData.setParaNum(0);
                                fileData.setPageNum(item.getPage());
                                fileData.setContent(item.getContent());
                                fileData.setCreateTime(currentDateString);
                                fileData.setCreateUser(file.getCreateUser());
                                fileData.setKnowledgeId(file.getKnowledgeId());
                                fileData.setFoldersId(file.getFoldersId());
                                fileData.setFileId(file.getFileId());
                                fileData.setFileName(file.getFileName());
                                fileData.setType(FileDataTypeConstants.FILE);
                                fileData.setParaNum(item.getPage());
                                fileData.setStatus("1");
                                fileData.setUpdateTime(currentDateString);
                                fileData.setUpdateUser(file.getUpdateUser());
                                denseVectorService.modelEncode(item.getContent(), fileData.getKnowledgeId(), fileData, CONTENT_DENSE_FILED);
                                fileDataList.add(fileData);
                            });
                            fileDataMapper.insertBatch(fileDataList);
                            // 更新统计
                            int total = fileDataList.stream().mapToInt(fileData -> fileData.getContent().length()).sum();
                            updateStatusSuccess(file, total, fileDataList.size());
                        }

                    } else {
                        updateStatusFail(file, "解析原文失败，接口请求失败");
                    }
                } else {
                    updateStatusFail(file, "解析原文失败，接口请求失败");
                }
            } else {
                updateStatusFail(file, "解析原文数据为空");
            }
        });
    }

    /**
     * 更新统计
     */
    private void updateStatusSuccess(File file, long total, int size) {
        UpdateChain.of(File.class)
                .set(FILE.WORD_COUNT, total)
                .set(FILE.STATUS, FileStatusEnum.SUCCESS.getCode())
                .set(FILE.PARAGRAPHS_NUM, size)
                .where(FILE.FILE_ID.eq(file.getFileId()))
                .update();
    }

    /**
     * 更新状态，失败
     */
    public static void updateStatusFail(File file, String msg) {
        UpdateChain.of(File.class)
                .set(FILE.STATUS, FileStatusEnum.FAIL.getCode())
                .set(FILE.ERROR_MSG, msg)
                .set(FILE.ERROR_NUM, file.getErrorNum() + 1)
                .where(FILE.FILE_ID.eq(file.getFileId()))
                .update();
    }

    /**
     * 更新状态，解析中
     */
    private void updateStatus(File file) {
        UpdateChain.of(File.class)
                .set(FILE.STATUS, FileStatusEnum.PARSING.getCode())
                .where(FILE.FILE_ID.eq(file.getFileId()))
                .update();
    }

    /**
     * 获取上传成功的文件，以便后续用于解析
     */
    private List<File> getFileList() {

        Wrappers wrappers = Wrappers.init()
                .select(FILE.ALL_COLUMNS, KNOWLEDGE_INFO.KNOWLEDGE_ID, KNOWLEDGE_INFO.DOCUMENT_ANALYSIS_SERVER)
                .from(FILE)
                .innerJoin(FOLDERS).on(FILE.FOLDERS_ID.eq(FOLDERS.FOLDERS_ID))
                .innerJoin(KNOWLEDGE_INFO).on(FOLDERS.KNOWLEDGE_ID.eq(KNOWLEDGE_INFO.KNOWLEDGE_ID))
                .and(FILE.FILE_URL.isNotNull())
                .and(FILE.FILE_URL.ne(StringConstant.BLANK))
                .and(FILE.STATUS.ne(FileStatusEnum.SUCCESS.getCode()))
                .and(FILE.STATUS.ne(FileStatusEnum.PARSING.getCode()))
                .and(FILE.TYPE.eq(FileTypeEnum.FILE.getType()))
                .and(FILE.FOLDERS_ID.isNotNull())
                .and(FILE.FOLDERS_ID.ne(StringConstant.BLANK))
                .and(KNOWLEDGE_INFO.KNOWLEDGE_ID.eq(knowledgeId))
                .and(FILE.ERROR_NUM.lt(5))
                .orderBy(FILE.CREATE_TIME.asc())
                .limit(100);
        List<File> list = fileService.list(wrappers);
        return list;
    }

}
