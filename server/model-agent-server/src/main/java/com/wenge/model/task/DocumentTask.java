package com.wenge.model.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.net.NetUtil;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.entity.File;
import com.wenge.model.enums.FileStatusEnum;
import com.wenge.model.enums.FileTypeEnum;
import com.wenge.model.mapper.es.FileDataMapper;
import com.wenge.model.service.FileService;
import com.wenge.oauth.constants.RedisConstant;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.wenge.model.entity.table.FileTableDef.FILE;
import static com.wenge.model.entity.table.FoldersTableDef.FOLDERS;
import static com.wenge.model.entity.table.KnowledgeInfoTableDef.KNOWLEDGE_INFO;

/**
 * 文档解析任务
 */
@RestController
@RequestMapping("/agentTask/document")
@Slf4j
public class DocumentTask {

    @Autowired
    private FileDocumentTask fileDocumentTask;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileDataMapper fileDataMapper;

    @Value("${task.document.enable}")
    private boolean enable;

    @Autowired
    private RedisService redisService;

    @Value("${task.hkDocument.knowledgeId}")
    private String excludeKnowledgeId;

    @PostConstruct
    public void init() {

        String value = redisService.get(RedisKey.PARSE_DOC, String.class);
        String localhostStr = NetUtil.getLocalhostStr();

        if (StringUtils.isNotBlank(value) && value.startsWith(localhostStr)) {
            redisService.del(RedisKey.PARSE_DOC);
            redisService.unlock(RedisConstant.LOCK_PARSE_DOC);
        }
    }

    /**
     * 解析文档
     */
    @RequestMapping("/documentAnalysis")
    // @Scheduled(cron = "${task.document.cron}")
    public void documentAnalysis() {
        long start01 = System.currentTimeMillis();
        log.info("==>解析文档定时器开始执行==== 时间毫秒： {}", start01);
        long start02 = 0L;
        // if (!enable) {
        //     start02 = System.currentTimeMillis();
        //     log.info("==>解析文档定时器 状态关闭==== 耗时毫秒： {}", (start02 - start01));
        //     return;
        // }

        boolean lock = redisService.lock(RedisConstant.LOCK_PARSE_DOC);
        long start03 = 0L;
        if (!lock) {
            log.info("==>任务未获取到锁，跳过当前任务");
            start03 = System.currentTimeMillis();
            log.info("==>解析文档定时器 未获取到锁==== 耗时毫秒： {}", (start03 - start02));
            return;
        }

        String redisKey = RedisKey.PARSE_DOC;
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
            fileDocumentTask.parseFile(fileList);
        } catch (Exception e) {
            log.error("解析文档定时器异常,error:{}", e.getMessage(), e);
        } finally {
            redisService.del(redisKey);
            redisService.unlock(RedisConstant.LOCK_PARSE_DOC);
        }
        long start07 = System.currentTimeMillis();
        log.info("==>解析文档定时器 开始查询解析详情==== 耗时毫秒： {}", (start07 - start06));
        log.info("==>解析文档定时器 本次执行总耗时==== 总耗时毫秒： {}", (start07 - start01));
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
                .and(KNOWLEDGE_INFO.KNOWLEDGE_ID.ne(excludeKnowledgeId))
                .and(FILE.ERROR_NUM.lt(5))
                .orderBy(FILE.CREATE_TIME.asc())
                .limit(100);
        List<File> list = fileService.list(wrappers);
        return list;
    }

}
