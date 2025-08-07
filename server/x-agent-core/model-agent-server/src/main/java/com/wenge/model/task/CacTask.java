package com.wenge.model.task;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wenge.model.dto.param.FileAddWebLinkParam;
import com.wenge.model.dto.param.FileAndLinkAddParam;
import com.wenge.model.entity.Folders;
import com.wenge.model.entity.ServiceMenu;
import com.wenge.model.enums.FileTypeEnum;
import com.wenge.model.enums.HeadStyleEnum;
import com.wenge.model.enums.KnowledgeTypeEnum;
import com.wenge.model.enums.ParagraphEnum;
import com.wenge.model.service.FileService;
import com.wenge.model.service.FoldersService;
import com.wenge.model.service.ServiceMenuService;
import com.wenge.model.utils.DateUtil;
import com.wenge.model.utils.MyMultipartFileVo;
import com.wenge.model.utils.MyWordUtil;
import com.wenge.model.utils.WordBean;
import com.wenge.model.workflow.constants.SettingConstants;
import com.wenge.oauth.constants.AppConfigContant;
import com.wenge.oauth.constants.RedisConstant;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 网信办
 */
@Slf4j
@RestController
@RequestMapping("/agentTask")
public class CacTask {


    @Autowired
    private FoldersService foldersService;

    @Autowired
    private FileService fileService;

    @Value("${task.cac.enable}")
    private Boolean enable;

    @Value("${task.cac.api}")
    private String cacApi;

    @Autowired
    private ServiceMenuService serviceMenuService;

    @Autowired
    private RedisService redisService;

    @PostConstruct
    public void init() {
        String value = redisService.get(RedisConstant.RUN_LH_ONLINE, String.class);
        String localhostStr = NetUtil.getLocalhostStr();

        if (StringUtils.isNotBlank(value) && value.startsWith(localhostStr)) {
            redisService.del(RedisConstant.RUN_LH_ONLINE);
            redisService.unlock(RedisConstant.LOCK_LH_ONLINE);
        }
    }

    /**
     * 更新文件
     *
     * @param fileName
     * @param folderId
     * @return
     */
    private com.wenge.model.entity.File updateFile(String fileName, String folderId, String currentTime) {
        com.wenge.model.entity.File fileFromDb = fileService.getByName(fileName, folderId);
        if (null == fileFromDb) {
            return null;
        }
        // 更新文件时间
        fileService.updateFileTime(fileFromDb.getFileId(), currentTime);

        return fileFromDb;
    }

    /**
     * 拉取网信办
     */
    @GetMapping("/cac")
    // @Scheduled(cron = "${task.cac.cron}")
    public void guide() {
        if (!enable) {
            log.info("==>网信办任务未开启，跳过当前任务");
            return;
        }

        try {

            boolean lock = redisService.lock(RedisConstant.LOCK_CAC);
            if (!lock) {
                log.info("==>网信办任务未获取到锁，跳过当前任务");
                return;
            }

            boolean hasKey = redisService.hasKey(RedisConstant.RUN_CAC);
            if (hasKey) {
                log.info("==>网信办任务正在执行，跳过当前任务");
                return;
            }

            String localhostStr = NetUtil.getLocalhostStr();
            String currentDateString = com.wg.appframe.core.utils.DateUtil.getCurrentDateString();
            redisService.set(RedisConstant.RUN_CAC, localhostStr + "_" + currentDateString, 60 * 60 * 24 * 7);

            String appId = AppConfigContant.getConfiguration(AppConfigContant.APP_CONFIG_CZC_GPT_APP_ID);
            if (StringUtils.isBlank(appId)) {
                log.info("==>网信办为配置应用id，跳过当前任务");
                return;
            }
            String knowledgeId = AppConfigContant.getConfiguration(AppConfigContant.APP_CONFIG_CAC_GPT_KNOWLEDGE_ID);
            if (StringUtils.isBlank(appId)) {
                log.info("==>网信办为配置知识库id，跳过当前任务");
                return;
            }

            log.info("==>拉取网信办知识库文件库开始");

            List<String> typeList = ListUtil.toList("cac_law");
            for (String type : typeList) {
                grabGuide(knowledgeId, appId, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisService.del(RedisConstant.RUN_CAC);
            redisService.unlock(RedisConstant.LOCK_CAC);
        }
        // 删除超时文件
        log.info("==>拉取网信办知识库文件库结束");
    }

    /**
     * 创建目录，并清空目录下所有文件
     *
     * @return
     */
    private String createGuideFolder(String knowledgeId, String folderName) {
        //String[] moduleArray = new String[]{"办事指南", menuName};
        String[] split = folderName.split("/");
        String parentId = "0";
        Folders folders;
        //FoldersEntity foldersEntity;
        for (String folder : split) {
            Folders byFoldersName = foldersService.getByFoldersName(folder, knowledgeId);
            if (null == byFoldersName) {
                folders = Folders.creat()
                        .setType(KnowledgeTypeEnum.DOCUMENT.getCode())
                        .setName(folder)
                        .setKnowledgeId(knowledgeId)
                        .setParentId(parentId);
                Result<Folders> result = foldersService.addFolders(folders);
                Folders data = result.getData();
                parentId = data.getFoldersId();
            } else {
                parentId = byFoldersName.getFoldersId();
            }
        }
        return parentId;
    }

    /**
     * 获取rul解析后的word文档地址
     *
     * @return
     */
    private void parseGuide(String type, ServiceMenu serviceMenu, String folderId, String currentTime) {
        JSONObject guideParam = new JSONObject();
        guideParam.put(SettingConstants.URL, serviceMenu.getMenuUrl());
        guideParam.put("start_url", serviceMenu.getMenuUrl());
        int pageNum = 1;
        List<JSONObject> resList;
        while (true) {
            try {
                guideParam.put("tar_page", pageNum);
                log.info("getFileUrl.请求url:{}，param:{}", cacApi, JSON.toJSONString(guideParam));
                String response = HttpUtil.post(cacApi, JSON.toJSONString(guideParam));
                log.info("getFileUrl.response:{}", response);
                if (StringUtils.isBlank(response) || !JSONUtil.isTypeJSONObject(response)) {
                    break;
                }
                JSONObject responseData = JSON.parseObject(response);
                if (!"200".equals(responseData.getString("status_code"))) {
                    log.info("getFileUrl.status_code is not 200");
                    break;
                }
                if (!responseData.containsKey("subUrl")) {
                    log.info("getFileUrl.resList为空");
                    break;
                }

                resList = responseData.getJSONArray("subUrl").toJavaList(JSONObject.class);
                if (CollectionUtils.isEmpty(resList)) {
                    log.info("getFileUrl.resList.filter 为空");
                    break;
                }

                resList.forEach(res -> {
                    dataList(res, folderId, currentTime);
                });

                pageNum++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 抓取办事指南
     * @param knowledgeId
     * @param appId
     * @param type
     */
    private void grabGuide(String knowledgeId, String appId, String type) {
        List<ServiceMenu> serviceMenuList = serviceMenuService.getGuideByAppId(appId, type);
        if (CollectionUtils.isEmpty(serviceMenuList)) {
            log.info("==>结束:{}", type);
            return;
        }
        log.info("==>拉取网信办知识库文件库开始，本次执行{}个任务", serviceMenuList.size());
        // 如果数据库和当前应用所在服务器存在时间差，这里可以防止数据库时间与当前时间不一致导致数据丢失
        String currentTime = LocalDateTimeUtil.format(LocalDateTimeUtil.now().minusHours(9), DateUtil.PATTERN_1);
        // String folderId = StringConstant.BLANK;
        // 是否批量的标记
        // boolean multiFlag = "lh_guide_multi".equals(type);

        serviceMenuList.forEach(serviceMenu -> {
            try {
                // 批量url时，创建目录
                String folderName = serviceMenu.getMenuName();
                String folderIds = createGuideFolder(knowledgeId, folderName);
                if (StringUtils.isBlank(folderIds)) {
                    return;
                }
                if (StringUtils.isNotBlank(folderIds)) {
                    // 获取办事指南数据
                    parseGuide(type.substring(type.lastIndexOf("_") + 1), serviceMenu, folderIds, currentTime);
                }
                // 批量抓取数据时，清理历史数据
                fileService.deleteFileLeTimeYayi(currentTime, knowledgeId, folderIds);
            } catch (Exception e) {
                log.error("拉取网信办知识库文件库error:{}", e.getMessage(), e);
            }
        });
    }


    /**
     * 数据处理
     *
     * @param jsonResponse
     * @param folderId
     */
    private void dataList(JSONObject jsonData, String folderId, String currentTime) {
        // JSONObject jsonData = JSON.parseObject(jsonResponse);
        String content = jsonData.getString("content");
        //String text = parse.text();
        String titleStr = jsonData.getString("title");
        String url = jsonData.getString("url");
        String fileName = titleStr + ".docx";

        // 更新文件时间
        com.wenge.model.entity.File fileFromEDb = updateFile(fileName, folderId, currentTime);
        if (null != fileFromEDb) {
            log.info("{}已上传成功，跳过当前文件", fileName);
            return;
        }

        // 创建一个新的文档
        XWPFDocument document = new XWPFDocument();
        List<WordBean> wordBeanList = Lists.newArrayList();
        WordBean title = new WordBean();
        title.setType(ParagraphEnum.TITLE);
        title.setTitleLevel(HeadStyleEnum.H_1);
        title.setText(titleStr);
        wordBeanList.add(title);

        String[] split = content.split("\n");
        WordBean text;
        for (String element : split) {
            text = new WordBean();
            text.setType(ParagraphEnum.TEXT);
            text.setText(element);
            wordBeanList.add(text);
        }

        MyWordUtil myWordUtil = new MyWordUtil(document);
        myWordUtil.createWord(wordBeanList);

        File file = new File(fileName);
        // 保存文档到文件
        FileOutputStream out = null;
        FileInputStream fis = null;
        try {
            out = new FileOutputStream(file);
            fis = new FileInputStream(file);
            // 将文档写入文件
            document.write(out);

            MultipartFile multipartFile = new MyMultipartFileVo(fileName, fileName, ContentType.APPLICATION_OCTET_STREAM.toString(), fis);
            FileAndLinkAddParam param = new FileAndLinkAddParam();

            param.setFoldersId(folderId);
            param.setType(FileTypeEnum.FILE.getType());

            List<FileAddWebLinkParam> files = Lists.newArrayList();
            param.setFiles(files);
            FileAddWebLinkParam fileAddWebLinkParam = new FileAddWebLinkParam();
            files.add(fileAddWebLinkParam);
            fileAddWebLinkParam.setFile(multipartFile);
            fileAddWebLinkParam.setWebLink(url);
            fileService.addFile(param);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            file.delete();
        }
    }
}

