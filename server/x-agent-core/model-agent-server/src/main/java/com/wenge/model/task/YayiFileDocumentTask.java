package com.wenge.model.task;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.net.NetUtil;
import com.google.common.collect.Maps;
import com.mybatisflex.core.update.UpdateChain;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.entity.File;
import com.wenge.model.entity.Folders;
import com.wenge.model.enums.FileStatusEnum;
import com.wenge.model.enums.FileTypeEnum;
import com.wenge.model.service.FileService;
import com.wenge.model.service.FoldersService;
import com.wenge.oauth.constants.RedisConstant;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.constants.StringConstans;
import com.wg.appframe.yayi.param.KnowledgeParam;
import com.wg.appframe.yayi.result.DocumentResult;
import com.wg.appframe.yayi.result.KnowledgeResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.FileTableDef.FILE;

/**
 * 雅意文档解析任务
 */
@RestController
@RequestMapping("/agentTask/yayiDocument")
@Slf4j
public class YayiFileDocumentTask {

    @Autowired
    private YayiServer yayiServer;

    @Autowired
    private FileService fileService;

    @Autowired
    private FoldersService foldersService;

    @Value("${task.document.enable}")
    private boolean enable;

    @Autowired
    private FileDocumentTask fileDocumentTask;

    public static final String FUNCTION_UPDATE_DOCS = "updateDocs";
    public static final String FUNCTION_DELETE_ROBOT = "deleteRobot";
    public static final String FUNCTION_DELETE_ROBOT_FILE = "deleteRobotFile";
    public static final String FUNCTION_GET_QUESTIONS = "getQuestions";
    public static final String FUNCTION_DOC_CHAT = "getAnswer";


    @Autowired
    private RedisService redisService;

    @PostConstruct
    public void init() {
        String value = redisService.get(RedisKey.PARSE_YAYI_DOC, String.class);
        String localhostStr = NetUtil.getLocalhostStr();
        if (StringUtils.isNotBlank(value) && value.startsWith(localhostStr)) {
            redisService.del(RedisKey.PARSE_YAYI_DOC);
            redisService.unlock(RedisConstant.LOCK_YAYI_DOC);
        }
    }

    /**
     * 解析文档
     */
    @RequestMapping("/documentAnalysis")
    // @Scheduled(cron = "${task.document.cron}")
    public void documentAnalysis() {
        log.info("==>yayi知识库解析文档开始");
        if (!enable) {
            return;
        }
        String redisKey = RedisKey.PARSE_YAYI_DOC;

        try {
            boolean lock = redisService.lock(RedisConstant.LOCK_YAYI_DOC);
            if (!lock) {
                log.info("==>任务未获取到锁，跳过当前任务");
                return;
            }

            if (redisService.hasKey(redisKey)) {
                log.info("定时器正在执行，本次跳过");
                return;
            }

            String localhostStr = NetUtil.getLocalhostStr();
            String currentDateString = com.wg.appframe.core.utils.DateUtil.getCurrentDateString();
            redisService.set(redisKey, localhostStr + "_" + currentDateString, 60 * 60 * 24 * 7);

            // 获取所有未成功解析的文件
            List<File> fileList = getFileList();
            if (CollectionUtil.isEmpty(fileList)) {
                return;
            }

            // 获取所有文件夹
            Map<String, Folders> folderMap = getFoldersList();

            // 遍历文件
            fileList.forEach(file -> {
                try {
                    // 更新状态，解析中
                    updateStatus(file);

                    // 清空该文件的所有es数据
                    String knowledgeId = folderMap.get(file.getFoldersId()).getKnowledgeId();
                    clearKnowledge(file, knowledgeId);

                    // 文档转文本
                    List<DocumentResult.FileContent> fileContents = fileDocumentTask.documentAnalysis(file);
                    if (CollectionUtil.isEmpty(fileContents)) {
                        updateStatusFail(file, "解析原文当数据为空");
                        return;
                    } else {
                        // 判断所有段落是否为空
                        boolean allMatch = fileContents.stream().allMatch(p -> StringUtils.isBlank(p.getText()));
                        if (allMatch) {
                            updateStatusFail(file, "解析原文当数据为空");
                        }
                    }

                    // 调用知识库能力，将文档上传到知识库
                    sendToKnowledge(fileContents, knowledgeId, file);

                    // 更新统计
                    int total = fileContents.stream().mapToInt(p -> p.getText().length()).sum();
                    updateStatusSuccess(file, total, fileContents.size());
                } catch (Exception e) {
                    log.error("解析文件失败:{},msg:{}", file.getFileId(), e.getMessage(), e);
                    // 更新状态，失败
                    updateStatusFail(file, e.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("解析要文档定时器异常,error:{}", e.getMessage(), e);

        } finally {
            redisService.del(redisKey);
        }
        log.info("==>yayi知识库解析文档结束");
    }


    /**
     * 获取上传成功的文件，以便后续用于解析
     */
    private List<File> getFileList() {
        Wrappers wrappers = Wrappers.init()
                .and(FILE.FILE_URL.isNotNull())
                .and(FILE.FILE_URL.ne(""))
                .and(FILE.STATUS.ne(FileStatusEnum.SUCCESS.getCode()))
                .and(FILE.TYPE.eq(FileTypeEnum.YAYI_DOC.getType()))
                .and(FILE.FOLDERS_ID.isNotNull())
                .and(FILE.FOLDERS_ID.ne(""))
                .and(FILE.ERROR_NUM.lt(5));
        List<File> list = fileService.list(wrappers);
        return list;
    }

    /**
     * 获取所有文件夹
     */
    private Map<String, Folders> getFoldersList() {
        List<Folders> foldersList = foldersService.list();
        return foldersList.stream().collect(Collectors.toMap(
                Folders::getFoldersId,
                p -> p,
                (k1, k2) -> k1,
                Maps::newHashMap
        ));
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
     * 清空该文件的所有数据
     */
    private void clearKnowledge(File file, String knowledgeId) {
        KnowledgeParam param = new KnowledgeParam();
        KnowledgeParam.Content knowledgeContent = new KnowledgeParam.Content();
        param.setContent(knowledgeContent);
        knowledgeContent.setFunction(FUNCTION_DELETE_ROBOT_FILE);
        knowledgeContent.setRobot_id(knowledgeId);
        knowledgeContent.setFile_id_list(ListUtil.toList(file.getFileId()));
        param.setModel(KnowledgeParam.KNOWLEDGE_CONFIG);
        log.info("[删除文档] 开始调用算法接口");
        KnowledgeResult knowledge = yayiServer.knowledge(param);
        if (!StringConstans.CODE.equals(knowledge.getCode())) {
            log.info("[删除文档] 成功");
        }
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
    private void updateStatusFail(File file, String msg) {
        UpdateChain.of(File.class)
                .set(FILE.STATUS, FileStatusEnum.FAIL.getCode())
                .set(FILE.ERROR_MSG, msg)
                .set(FILE.ERROR_NUM, file.getErrorNum() + 1)
                .where(FILE.FILE_ID.eq(file.getFileId()))
                .update();
    }

    /**
     * 发送到知识库
     * @param fileContents
     * @param knowledgeId
     * @param file
     */
    private void sendToKnowledge(List<DocumentResult.FileContent> fileContents, String knowledgeId, File file) {
        List<KnowledgeParam.FileContent> fileContentList = fileContents.stream().map(p -> {
            KnowledgeParam.FileContent bean = BeanUtil.toBean(p, KnowledgeParam.FileContent.class);
            KnowledgeParam.FileInfo info = bean.getInfo();
            if (null != info) {
                info.setFile_id(file.getFileId());
            }
            // 标题为空，则设置为空字符串
            if (StringUtils.isBlank(bean.getTitle())) {
                bean.setTitle(StringConstans.BLANK);
            }
            if (StringUtils.isBlank(bean.getType())) {
                bean.setType("text");
            }
            return bean;
        }).collect(Collectors.toList());
        KnowledgeParam param = new KnowledgeParam();
        KnowledgeParam.Content knowledgeContent = new KnowledgeParam.Content();
        param.setContent(knowledgeContent);
        knowledgeContent.setFunction(FUNCTION_UPDATE_DOCS);
        knowledgeContent.setRobot_id(knowledgeId);
        knowledgeContent.setFile_content(fileContentList);
        param.setModel(KnowledgeParam.KNOWLEDGE_CONFIG);
        KnowledgeResult knowledge = yayiServer.knowledge(param);
        if (!StringConstans.CODE.equals(knowledge.getCode())) {
            updateStatusFail(file, knowledge.getMsg());
        }
    }

}
