package com.wenge.model.task;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wenge.model.dto.param.FileAddWebLinkParam;
import com.wenge.model.dto.param.FileAndLinkAddParam;
import com.wenge.model.entity.Folders;
import com.wenge.model.entity.LhOnline;
import com.wenge.model.entity.ServiceMenu;
import com.wenge.model.enums.FileTypeEnum;
import com.wenge.model.enums.HeadStyleEnum;
import com.wenge.model.enums.KnowledgeTypeEnum;
import com.wenge.model.enums.ParagraphEnum;
import com.wenge.model.service.FileService;
import com.wenge.model.service.FoldersService;
import com.wenge.model.service.LhOnlineService;
import com.wenge.model.service.ServiceMenuService;
import com.wenge.model.utils.DateUtil;
import com.wenge.model.utils.MyMultipartFileVo;
import com.wenge.model.utils.MyWordUtil;
import com.wenge.model.utils.WordBean;
import com.wenge.model.workflow.constants.SettingConstants;
import com.wenge.oauth.constants.AppConfigContant;
import com.wenge.oauth.constants.RedisConstant;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 龙华在线数据拉取
 */
@Slf4j
@RestController
@RequestMapping("/agentTask")
public class LhOnlineTask {

    private static String NOTICE_URL = "https://www.szlhq.gov.cn/postmeta/i/4286.json";

    @Autowired
    private FoldersService foldersService;

    @Autowired
    private FileService fileService;

    @Autowired
    private LhOnlineService lhOnlineService;

    @Value("${task.lhonline.enable}")
    private Boolean enable;

    @Value("${task.guide.enable}")
    private Boolean guideEnable;

    @Value("${task.guide.api}")
    private String guideApi;

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

        value = redisService.get(RedisConstant.RUN_LH_ONLINE_GUI, String.class);
        localhostStr = NetUtil.getLocalhostStr();
        if (StringUtils.isNotBlank(value) && value.startsWith(localhostStr)) {
            redisService.del(RedisConstant.RUN_LH_ONLINE_GUI);
            redisService.unlock(RedisConstant.LOCK_LH_ONLINE_GUI);
        }
    }


    /**
     * 创建目录，并清空目录下所有文件
     *
     * @param onlineInfo
     * @return
     */
    private String createFolder(LhOnline onlineInfo, String knowledgeId) {
        String module = onlineInfo.getDirectory();
        String[] moduleArray = module.split("/");
        String parentId = "0";
        Folders folders;
        for (String folder : moduleArray) {
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
     * 拉取龙华在线数据到知识库文件库
     */
    @GetMapping("/report")
    // @Scheduled(cron = "${task.lhonline.cron}")
    public void report() {
        String knowledgeId = AppConfigContant.getConfiguration(AppConfigContant.APP_CONFIG_LONGHUA_GPT_KNOWLEDGE_ID);
        if (!enable || StringUtils.isBlank(knowledgeId)) {
            return;
        }
        try {
            boolean lock = redisService.lock(RedisConstant.LOCK_LH_ONLINE);
            if (!lock) {
                log.info("==>任务未获取到锁，跳过当前任务");
                return;
            }

            boolean hasKey = redisService.hasKey(RedisConstant.RUN_LH_ONLINE);
            if (hasKey) {
                log.info("==>任务正在执行，跳过当前任务");
                return;
            }
            String localhostStr = NetUtil.getLocalhostStr();
            String currentDateString = com.wg.appframe.core.utils.DateUtil.getCurrentDateString();
            redisService.set(RedisConstant.RUN_LH_ONLINE, localhostStr + "_" + currentDateString, 60 * 60 * 24 * 7);

            log.info("==>拉取龙华在线数据到知识库文件库开始");
            List<LhOnline> list = lhOnlineService.getActiveList();
            if (CollectionUtils.isEmpty(list)) {
                log.info("==>拉取龙华在线数据到知识库文件库结束");
                return;
            }
            log.info("==>拉取龙华在线数据到知识库文件库开始，本次执行{}个任务", list.size());

            list.forEach(p -> {
                try {
                    // 创建目录
                    String folderId = createFolder(p, knowledgeId);
                    if (null != folderId) {
                        // 获取html数据
                        runDetail(p, folderId, knowledgeId);
                    }
                } catch (Exception e) {
                    log.error("拉取龙华在线数据到知识库文件库error:{}", e.getMessage(), e);
                }
            });
            log.info("==>拉取龙华在线数据到知识库文件库结束");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisService.del(RedisConstant.RUN_LH_ONLINE);
            redisService.unlock(RedisConstant.LOCK_LH_ONLINE);
        }

    }

    /**
     * 运行详情
     */
    private void runDetail(LhOnline onlineInfo, String folderId, String knowledgeId) {
        try {
            String baseUrl = "https://www.szlhq.gov.cn/postmeta/i/{columnId}.json".replace("{columnId}", onlineInfo.getColumnId());
            log.info("==>龙华在线数据拉取【{}】定时器启动，baseUrl：【{}】", onlineInfo.getColumnName(), baseUrl);
            String response = HttpUtil.get(baseUrl);
            //log.info("==>report.response:{}", response);
            List<String> idList = Lists.newArrayList();
            if (StringUtils.isNotBlank(response) && JSONUtil.isTypeJSONObject(response)) {
                JSONObject jsonObject = JSON.parseObject(response);
                if (jsonObject.containsKey("articles")) {
                    List<JSONObject> articles = jsonObject.getJSONArray("articles").toJavaList(JSONObject.class);
                    if (CollectionUtils.isNotEmpty(articles)) {
                        idList.addAll(articles.stream().map(p -> p.getString("id")).distinct().collect(Collectors.toList()));
                    }
                }
            }
            log.info("==>总共有{}篇数据", idList.size());
            if (CollectionUtils.isEmpty(idList)) {
                return;
            }
            // 龙华在线提取文章的url规则
            List<String> urlList = idList.stream().map(p ->
                            "https://www.szlhq.gov.cn/postmeta/p/" + (int) Math.floor(Long.parseLong(p) / 1000000) + "/" + (int) Math.floor(Long.parseLong(p) / 1000) + "/" + p + ".json")
                    .distinct().collect(Collectors.toList());
            String currentTime = LocalDateTimeUtil.format(LocalDateTimeUtil.now().minusHours(9), DateUtil.PATTERN_1);
            urlList.forEach(url -> {
                try {
                    String jsonResponse = HttpUtil.get(url);
                    if (StringUtils.isNotBlank(jsonResponse) && JSONUtil.isTypeJSONObject(jsonResponse)) {
                        // 获取数据
                        dataList(jsonResponse, folderId, currentTime);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // 删除超时文件
            fileService.deleteFileLeTimeYayi(currentTime, knowledgeId, folderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据处理
     *
     * @param jsonResponse
     * @param folderId
     */
    private void dataList(String jsonResponse, String folderId, String currentTime) {
        JSONObject jsonData = JSON.parseObject(jsonResponse);
        String content = jsonData.getString("content");
        Document parse = Jsoup.parse(content);
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

        Elements elements = parse.select("p");
        WordBean text;
        for (Element element : elements) {
            text = new WordBean();
            text.setType(ParagraphEnum.TEXT);
            text.setText(element.text());
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
     * 拉取龙华办事指南
     */
    @GetMapping("/guide")
    // @Scheduled(cron = "${task.guide.cron}")
    public void guide() {
        if (!guideEnable) {
            log.info("==>龙华在线办事指南任务未开启，跳过当前任务");
            return;
        }
        try {
            boolean lock = redisService.lock(RedisConstant.LOCK_LH_ONLINE_GUI);
            if (!lock) {
                log.info("==>任务未获取到锁，跳过当前任务");
                return;
            }

            boolean hasKey = redisService.hasKey(RedisConstant.RUN_LH_ONLINE_GUI);
            if (hasKey) {
                log.info("==>任务正在执行，跳过当前任务");
                return;
            }

            String localhostStr = NetUtil.getLocalhostStr();
            String currentDateString = com.wg.appframe.core.utils.DateUtil.getCurrentDateString();
            redisService.set(RedisConstant.RUN_LH_ONLINE_GUI, localhostStr + "_" + currentDateString, 60 * 60 * 24 * 7);

            String appId = AppConfigContant.getConfiguration(AppConfigContant.APP_CONFIG_LONGHUA_GPT_APP_ID);
            if (StringUtils.isBlank(appId)) {
                log.info("==>龙华在线办事指南为配置应用id，跳过当前任务");
                return;
            }
            String knowledgeId = AppConfigContant.getConfiguration(AppConfigContant.APP_CONFIG_LONGHUA_GPT_KNOWLEDGE_ID);
            if (StringUtils.isBlank(appId)) {
                log.info("==>龙华在线办事指南为配置知识库id，跳过当前任务");
                return;
            }

            log.info("==>拉取龙华办事指南知识库文件库开始");

            List<String> typeList = ListUtil.toList("lh_guide_single", "lh_guide_multi", "lh_guide_one_thing_one_time_single");
            for (String type : typeList) {
                grabGuide(knowledgeId, appId, type);
            }
            // 删除超时文件
            log.info("==>拉取龙华办事指南知识库文件库结束");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisService.del(RedisConstant.RUN_LH_ONLINE_GUI);
            redisService.unlock(RedisConstant.LOCK_LH_ONLINE_GUI);
        }
    }

    /**
     * 创建目录，并清空目录下所有文件
     *
     * @return
     */
    private String createGuideFolder(String knowledgeId, String folderName) {
        //String[] moduleArray = new String[]{"办事指南", menuName};
        List<String> allFolderList = ListUtil.toList("广东政务", "龙华办事指南");
        if (StringUtils.isNotBlank(folderName)) {
            allFolderList.add(folderName);
        }
        String parentId = "0";
        Folders folders;
        for (String folder : allFolderList) {
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
        guideParam.put("type", type);
        if ("lh_guide_one_thing_one_time_single".equals(serviceMenu.getServiceCode())) {
            guideParam.put("pageType", "allInOne");
        }

        int pageNum = 1;
        List<JSONObject> resList;
        while (true) {
            try {
                if ("multi".equals(type)) {
                    guideParam.put("pageNum", pageNum);
                }
                log.info("getFileUrl.请求url:{}，param:{}", guideApi, JSON.toJSONString(guideParam));
                String response = HttpUtil.post(guideApi, JSON.toJSONString(guideParam));
                log.info("getFileUrl.response:{}", response);
                if (StringUtils.isBlank(response) || !JSONUtil.isTypeJSONObject(response)) {
                    break;
                }
                JSONObject responseData = JSON.parseObject(response);
                if (!"200".equals(responseData.getString("status_code"))) {
                    log.info("getFileUrl.status_code is not 200");
                    break;
                }
                if (!responseData.containsKey("res")) {
                    log.info("getFileUrl.resList为空");
                    break;
                }

                resList = responseData.getJSONArray("res").toJavaList(JSONObject.class);
                if (CollectionUtils.isEmpty(resList)) {
                    log.info("getFileUrl.resList.filter 为空");
                    break;
                }

                // 保存文件
                saveFile(resList, serviceMenu, folderId, currentTime);
                // 单文件抓取结束，单文件只需要抓取一次即可
                if ("single".equals(type)) {
                    log.info("getFileUrl.single.break");
                    break;
                }
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
        log.info("==>拉取龙华办事指南知识库文件库开始，本次执行{}个任务", serviceMenuList.size());
        // 如果数据库和当前应用所在服务器存在时间差，这里可以防止数据库时间与当前时间不一致导致数据丢失
        String currentTime = LocalDateTimeUtil.format(LocalDateTimeUtil.now().minusHours(9), DateUtil.PATTERN_1);
        String folderId = StringConstant.BLANK;
        // 是否批量的标记
        boolean multiFlag = "lh_guide_multi".equals(type);

        // 非批量url的提前创建目录
        if (!multiFlag) {
            String folderName = StringConstant.BLANK;
            if ("lh_guide_one_thing_one_time_single".equals(type)) {
                folderName = "一件事一次办";
            }
            folderId = createGuideFolder(knowledgeId, folderName);
        }

        String folderIdTemp = folderId;
        serviceMenuList.forEach(serviceMenu -> {
            try {
                // 批量url时，创建目录
                String folderIds = StringConstant.BLANK;
                if (multiFlag) {
                    String folderName = serviceMenu.getMenuName();
                    folderIds = createGuideFolder(knowledgeId, folderName);
                } else {
                    folderIds = folderIdTemp;
                }

                if (StringUtils.isBlank(folderIds)) {
                    return;
                }
                if (StringUtils.isNotBlank(folderIds)) {
                    // 获取办事指南数据
                    parseGuide(type.substring(type.lastIndexOf("_") + 1), serviceMenu, folderIds, currentTime);
                }
                // 批量抓取数据时，清理历史数据
                if (multiFlag) {
                    fileService.deleteFileLeTimeYayi(currentTime, knowledgeId, folderIds);
                }
            } catch (Exception e) {
                log.error("拉取龙华办事指南知识库文件库error:{}", e.getMessage(), e);
            }
        });

        // 非批量抓取数据时，清理历史数据
        if (!multiFlag) {
            fileService.deleteFileLeTimeYayi(currentTime, knowledgeId, folderId);
        }
    }

    /**
     * 保存文件
     * @param resList
     * @param serviceMenu
     * @param folderId
     * @param currentTime
     */
    private void saveFile(List<JSONObject> resList, ServiceMenu serviceMenu, String folderId, String currentTime) {
        // 遍历解析后的word文档
        resList.forEach(res -> {
            InputStream inputStream = null;
            String fileName = res.getString("fileName") + ".docx";
            try {
                com.wenge.model.entity.File fileFromEDb = updateFile(fileName, folderId, currentTime);
                if (null != fileFromEDb) {
                    log.info("{}已上传成功，跳过当前文件", fileName);
                    return;
                }
                FileAndLinkAddParam param = new FileAndLinkAddParam();
                param.setFoldersId(folderId);
                param.setType(FileTypeEnum.FILE.getType());
                List<FileAddWebLinkParam> files = Lists.newArrayList();
                param.setFiles(files);
                FileAddWebLinkParam fileAddWebLinkParam = new FileAddWebLinkParam();
                files.add(fileAddWebLinkParam);

                String fileUrl = res.getString("fileUrl");
                // 下载文档
                HttpResponse httpResponse = HttpRequest.get(fileUrl).execute();

                if (httpResponse.isOk()) {
                    inputStream = httpResponse.bodyStream();
                    MultipartFile multipartFile = new MyMultipartFileVo(fileName, fileName, ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream);
                    fileAddWebLinkParam.setFile(multipartFile);
                    fileAddWebLinkParam.setWebLink(serviceMenu.getMenuUrl());

                    // 如果有返回的文件链接，则使用此链接
                    if (StringUtils.isNotBlank(res.getString("websiteUrl"))) {
                        fileAddWebLinkParam.setWebLink(res.getString("websiteUrl"));
                    }
                    fileAddWebLinkParam.setFileUrl(fileUrl);
                    fileService.addFile(param);
                } else {
                    System.err.println("请求失败，状态码: " + httpResponse.getStatus());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != inputStream) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    File file = new File(fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

