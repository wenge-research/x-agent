package com.wenge.model.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.mybatisflex.core.query.QueryColumn;
import com.wenge.model.config.WebSocketServer;
import com.wenge.model.constants.KnowledgeConstant;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.dto.param.DeleteKnowledgeDataParam;
import com.wenge.model.dto.param.KnowledgeDataPageParam;
import com.wenge.model.dto.param.importKnowledgeDataParam;
import com.wenge.model.dto.result.KnowledgeDataResult;
import com.wenge.model.dto.result.KnowledgeDataScopeResult;
import com.wenge.model.entity.*;
import com.wenge.model.entity.table.KnowledgeInfoTableDef;
import com.wenge.model.enums.WebsocketTypeEnums;
import com.wenge.model.mapper.es.KnowledgeDataMapper;
import com.wenge.model.service.DenseVectorService;
import com.wenge.model.service.KnowledgeDataService;
import com.wenge.model.service.KnowledgeDataTypeService;
import com.wenge.model.service.KnowledgeInfoService;
import com.wenge.model.utils.AnswerUtils;
import com.wenge.model.utils.EasyExcelUtil;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import com.wg.appframe.yayi.param.RearrangeParam;
import com.wg.appframe.yayi.result.RearrangeResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.conditions.update.LambdaEsUpdateWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.*;
import static com.wenge.model.entity.table.KnowledgeDataTypeTableDef.KNOWLEDGE_DATA_TYPE;
import static com.wenge.model.entity.table.KnowledgeInfoTableDef.KNOWLEDGE_INFO;

@Service
@Slf4j
public class knowledgeDataServiceImpl implements KnowledgeDataService {

    @Autowired
    private KnowledgeDataMapper dataMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private KnowledgeInfoService knowledgeInfoService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private KnowledgeDataTypeService typeService;

    @Autowired
    private DenseVectorService denseVectorService;

    @Autowired
    private AnswerUtils answerUtils;

    @PostConstruct
    public void init() {
        try {
            // 启动时，清空导入中状态
            String key = RedisKey.IMPORTING;
            redisService.batchDel(key);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Result addKnowledgeData(KnowledgeData knowledgeData) {
        String accountName = AppContextHolder.getAccountName();
        // 验证知识库是否存在
        if (StringUtils.isBlank(knowledgeData.getKnowledgeId())) {
            return Result.fail("知识库不存在");
        }

        Wrappers wrappers = Wrappers.init()
                .where(KnowledgeInfoTableDef.KNOWLEDGE_INFO.KNOWLEDGE_ID.eq(knowledgeData.getKnowledgeId()))
                .limit(1);
        KnowledgeInfo knowledgeInfo = knowledgeInfoService.getOne(wrappers);
        if (null == knowledgeInfo) {
            return Result.fail("知识库不存在");
        }

        //if (!knowledgeTypeEnum.Q_A.getCode().equals(knowledgeInfo.getKnowledgeType())) {
        //    return Result.fail("知识库类型必须为" + knowledgeTypeEnum.Q_A.getName());
        //}

        // 向量化标题和内容
        if (StringUtils.isNotBlank(knowledgeData.getTitle())) {
            List<Double> doubles = denseVectorService.modelEncode(knowledgeData.getTitle(), knowledgeData.getKnowledgeId(), knowledgeData, TITLE_DENSE_FILED);
            if (CollectionUtil.isEmpty(doubles)) {
                return Result.fail("内容向量化失败");
            }
        }
        if (StringUtils.isNotBlank(knowledgeData.getContent())) {
            List<Double> doubles = denseVectorService.modelEncode(knowledgeData.getContent(), knowledgeInfo.getKnowledgeId(), knowledgeData, CONTENT_DENSE_FILED);
            if (CollectionUtil.isEmpty(doubles)) {
                return Result.fail("内容向量化失败");
            }
        }
        String format = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.DEFAULT_FORMAT);
        knowledgeData.setUpdateTime(format);
        knowledgeData.setUpdateUser(accountName);
        if (StringUtils.isBlank(knowledgeData.getSuggest())) {
            knowledgeData.setSuggest("推荐");
        }
        if (CollectionUtil.isEmpty(knowledgeData.getContentDense()) && CollectionUtil.isEmpty(knowledgeData.getContentDense1024())) {
            return Result.fail("内容向量化失败");
        }

        if (StringUtils.isBlank(knowledgeData.getPolishFlag())) {
            knowledgeData.setPolishFlag(YesNoEnum.YES.getName());
        }

        if (StringUtils.isNotBlank(knowledgeData.getId())) {
            dataMapper.updateById(knowledgeData);
        } else {
            knowledgeData.setCreateTime(format);
            knowledgeData.setCreateUser(accountName);
            dataMapper.insert(knowledgeData);
        }

        return Result.success();

    }

    @Override
    public EsPageInfo<KnowledgeData> getDeleteKnowledgeData(KnowledgeDataPageParam param) {

        LambdaEsQueryWrapper<KnowledgeData> wrapper = EsWrappers.lambdaQuery(KnowledgeData.class)
                .notSelect(KnowledgeData::getContentDense, KnowledgeData::getTitleDense)
                .eq(KnowledgeData::getDelStatus, "1")
                .eq(StringUtils.isNotBlank(param.getKnowledgeId()), KnowledgeData::getKnowledgeId, param.getKnowledgeId());

        if (StringUtils.isNotBlank(param.getUpdateTimeStart()) && StringUtils.isNotBlank(param.getUpdateTimeStart())) {
            wrapper.between(KnowledgeData::getUpdateTime, param.getUpdateTimeStart(), param.getUpdateTimeEnd());
        }
        if (StringUtils.isNotBlank(param.getTitle())) {
            wrapper.matchPhrasePrefixQuery(StringUtils.isNotBlank(param.getContent()), KnowledgeData::getTitle, param.getTitle());
        }
        wrapper.orderByDesc(KnowledgeData::getUpdateTime);
        EsPageInfo<KnowledgeData> knowledgeDataEsPageInfo = dataMapper.pageQuery(wrapper, param.getPageNo(),
                param.getPageSize());

        return knowledgeDataEsPageInfo;
    }

    @Override
    public Result<EsPageInfo<KnowledgeDataResult>> getKnowledgeDataList(KnowledgeDataPageParam param) {
        // 获取分类名称
        Map<Long, String> typeMap = typeService.getTypeMap(param.getKnowledgeId());

        // 获取知识库数据
        EsPageInfo<KnowledgeData> knowledgeDataEsPageInfo = getKnowledgeDatas(param);
        List<KnowledgeData> list = knowledgeDataEsPageInfo.getList();

        // 封装数据
        List<KnowledgeDataResult> collect = list.stream().map(p -> {
            // 获取分类名称
            KnowledgeDataResult bean = BeanUtil.toBean(p, KnowledgeDataResult.class);
            if (StringUtils.isBlank(bean.getCategory())) {
                return bean;
            }
            String[] split = bean.getCategory().split("/");
            List<String> categoryNameList = Lists.newArrayList();
            for (String typeId : split) {
                categoryNameList.add(typeMap.getOrDefault(Long.parseLong(typeId), StringConstant.BLANK));
            }
            bean.setCategoryName(String.join("/", categoryNameList));
            return bean;
        }).collect(Collectors.toList());
        EsPageInfo<KnowledgeDataResult> pageInfo = new EsPageInfo<>();
        pageInfo.setTotal(knowledgeDataEsPageInfo.getTotal());
        pageInfo.setList(collect);
        return Result.success(pageInfo);
    }

    /**
     * 获取知识库列表
     *
     * @param param
     * @return
     */
    private EsPageInfo<KnowledgeData> getKnowledgeDatas(KnowledgeDataPageParam param) {
        String knowledgeId = param.getKnowledgeId();
        List<String> cateGoryIdList = new ArrayList<>();
        List<KnowledgeDataType> dataTypeList = new ArrayList<>();
        if (StringUtils.isNotBlank(knowledgeId)) {
            // 获取数据分类主键id
            String cateGory = param.getCategory();
            if (StringUtils.isNotBlank(cateGory)) {
                // 获取当前分类id
                String[] split = cateGory.split("/");
                String currentCateGoryIdStr = split[split.length - 1];
                long currentCateGoryId = Long.parseLong(currentCateGoryIdStr);
                Wrappers<Object> cateGoryWrappers = Wrappers.init()
                        .where(KNOWLEDGE_DATA_TYPE.KNOWLEDGE_ID.eq(knowledgeId)).and(KNOWLEDGE_DATA_TYPE.DELETE_FLAG.eq(0));
                // 获取当前知识库下的所有分类
                List<KnowledgeDataType> allDataTypeList = typeService.list(cateGoryWrappers);
                dataTypeList = typeService.getTreeById(currentCateGoryId, allDataTypeList);

                // 拼接分类id
                Map<Long, List<KnowledgeDataType>> dataTypeMap = dataTypeList.stream().collect(Collectors.groupingBy(KnowledgeDataType::getPid));
                dataTypeList.forEach(knowledgeDataType -> {
                    knowledgeDataType.setChildren(dataTypeMap.get(knowledgeDataType.getId()));
                });
                // 获取当前分类（内含children）
                Optional<KnowledgeDataType> any = dataTypeList.stream().filter(p -> p.getId() == currentCateGoryId).findAny();
                if (any.isPresent()) {
                    KnowledgeDataType dataType = any.get();
                    List<KnowledgeDataType> initKnowledgeDataTypeList = new ArrayList<>();
                    initKnowledgeDataTypeList.add(dataType);
                    String cateGoryParentIdStr = "";
                    if (split.length > 1) {
                        // 存在分类的父id
                        String[] result = Arrays.copyOfRange(split, 0, split.length - 1);
                        cateGoryParentIdStr = String.join("/", result);
                    }
                    cateGoryIdList = getPaths(initKnowledgeDataTypeList, cateGoryParentIdStr);
                    cateGoryIdList.add(cateGory);
                }
            }
        }

        LambdaEsQueryWrapper<KnowledgeData> wrapper = EsWrappers.lambdaQuery(KnowledgeData.class)
                .notSelect(KnowledgeData::getContentDense, KnowledgeData::getTitleDense)
                .in(CollectionUtil.isNotEmpty(cateGoryIdList), KnowledgeData::getCategory, cateGoryIdList)
                .eq(KnowledgeData::getDelStatus, "0")
                .eq(StringUtils.isNotBlank(param.getKnowledgeId()), KnowledgeData::getKnowledgeId, param.getKnowledgeId())
                .exists(param.isNeedParam(), KnowledgeData::getCreateUser) // 字段存在
                .matchPhrasePrefixQuery(StringUtils.isNotBlank(param.getContent()), KnowledgeData::getContent, param.getContent())
                .in(CollectionUtils.isNotEmpty(param.getKnowledgeIds()), KnowledgeData::getKnowledgeId, param.getKnowledgeIds())
                // 合并标题和内容，当title不为空的时候，同时匹配标题和内容
                .and(StringUtils.isNotBlank(param.getTitle()), w -> {
                    w.or(t -> {
                        t.matchPhrasePrefixQuery(KnowledgeData::getTitle, param.getTitle());
                    });
                    w.or(t -> {
                        t.matchPhrasePrefixQuery(KnowledgeData::getContent, param.getTitle());
                    });
                })
                .eq(StringUtils.isNotBlank(param.getAccurate()), KnowledgeData::getAccurate, param.getAccurate())
                .orderByDesc(KnowledgeData::getUpdateTime);

        EsPageInfo<KnowledgeData> knowledgeDataEsPageInfo = dataMapper.pageQuery(wrapper, param.getPageNo(),
                param.getPageSize());
        return knowledgeDataEsPageInfo;
    }

    @Override
    public Result<String> importKnowledgeData(importKnowledgeDataParam param) {
        // 唯一表述，前端根据id判断是否当前任务的进度
        String requestId = UUID.randomUUID().toString();
        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

        Wrappers<Object> wrappers = Wrappers.init()
                .where(KNOWLEDGE_INFO.KNOWLEDGE_ID.eq(param.getKnowledgeId()))
                .limit(1);
        KnowledgeInfo knowledgeInfo = knowledgeInfoService.getOne(wrappers);
        if (null == knowledgeInfo) {
            return Result.fail("知识库不存在");
        }

        //if (!knowledgeTypeEnum.Q_A.getCode().equals(knowledgeInfo.getKnowledgeType())) {
        //    return Result.fail("知识库类型必须为" + knowledgeTypeEnum.Q_A.getName());
        //}
        String accountName = AppContextHolder.getAccountName();
        String key = RedisKey.IMPORTING + (StringUtils.isNotBlank(accountName) ? accountName : "admin");
        if (redisService.hasKey(key)) {
            return Result.fail("当前有其他导入任务在进行中");
        }
        log.info("===>开始导入知识库数据");
        MultipartFile file = param.getFile();
        if (file == null) {
            return Result.fail("文件不能为空");
        } else if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")) {
            return Result.fail("不支持该文件类型，请上传xlsx类型的文件");
        }

        // 异步导入数据
        redisService.set(key, param.getKnowledgeId(), 60 * 60 * 24 * 7);
        new Thread(() -> {
            try {
                importData(file, param, tokenUserInfo, requestId);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisService.del(key);
            }
        }).start();
        return Result.success(requestId);
    }

    @Override
    public void downloadKnowledgeDataTemp(HttpServletResponse response) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            Resource resource = resourceLoader.getResource("classpath:temp/knowledgeDataTemp.xlsx");
            inputStream = resource.getInputStream();

            // 设置响应头
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            // 获取文件名，并进行UTF-8编码
            String fileName = URLEncoder.encode("知识库数据模板.xlsx", StandardCharsets.UTF_8.toString());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            // 将文件写入响应输出流
            outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != outputStream) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Result recoverKnowledgeData(DeleteKnowledgeDataParam param) {
        String accountName = AppContextHolder.getAccountName();
        if (CollectionUtil.isNotEmpty(param.getId())) {
            LambdaEsUpdateWrapper<KnowledgeData> wrapper = EsWrappers.lambdaUpdate(KnowledgeData.class)
                    .in(KnowledgeData::getId, param.getId());
            KnowledgeData knowledgeData = new KnowledgeData();
            knowledgeData.setDelStatus("0");
            knowledgeData.setUpdateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.DEFAULT_FORMAT));
            knowledgeData.setUpdateUser(accountName);
            dataMapper.update(knowledgeData, wrapper);
        }

        if (CollectionUtil.isNotEmpty(param.getKnowledgeId())) {
            LambdaEsUpdateWrapper<KnowledgeData> wrapper = EsWrappers.lambdaUpdate(KnowledgeData.class)
                    .in(KnowledgeData::getKnowledgeId, param.getKnowledgeId());
            KnowledgeData knowledgeData = new KnowledgeData();
            knowledgeData.setDelStatus("0");
            knowledgeData.setUpdateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.DEFAULT_FORMAT));
            knowledgeData.setUpdateUser(accountName);
            dataMapper.update(knowledgeData, wrapper);
        }
        return Result.success();
    }

    @Override
    public Result deleteKnowledgeData(DeleteKnowledgeDataParam param) {
        String accountName = AppContextHolder.getAccountName();
        if (CollectionUtil.isNotEmpty(param.getId())) {
            LambdaEsUpdateWrapper<KnowledgeData> wrapper = EsWrappers.lambdaUpdate(KnowledgeData.class)
                    .in(KnowledgeData::getId, param.getId());
            KnowledgeData knowledgeData = new KnowledgeData();
            knowledgeData.setDelStatus("1");
            knowledgeData.setUpdateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.DEFAULT_FORMAT));
            knowledgeData.setUpdateUser(accountName);
            dataMapper.update(knowledgeData, wrapper);
        }
        if (CollectionUtil.isNotEmpty(param.getKnowledgeId())) {
            LambdaEsUpdateWrapper<KnowledgeData> wrapper = EsWrappers.lambdaUpdate(KnowledgeData.class)
                    .in(KnowledgeData::getKnowledgeId, param.getKnowledgeId());
            KnowledgeData knowledgeData = new KnowledgeData();
            knowledgeData.setUpdateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.DEFAULT_FORMAT));
            knowledgeData.setDelStatus("1");
            knowledgeData.setUpdateUser(accountName);
            dataMapper.update(knowledgeData, wrapper);
        }
        return Result.success();
    }

    @Override
    public void exportData(KnowledgeDataPageParam param, HttpServletResponse response) {
        param.setPageNo(1);
        param.setPageSize(9999);
        // 查询
        EsPageInfo<KnowledgeData> vector = getKnowledgeDatas(param);
        List<KnowledgeData> list = vector.getList();
        String fileName = "export.xls";
        File file = null;
        try {
            // 设置响应头
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            // 标题	内容	分类(一级/二级)	是否推荐(推荐/不推荐)	链接	是否精确(是/否) 是否需要润色
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            OutputStream outputStream = response.getOutputStream();
            //标题内容、内容、主题、是否推荐、链接、是否需要润色、是否精确、创建时间、创建人、部门这些都可以导出
            List<List<String>> rows = Lists.newArrayList();
            List<String> titleList = ListUtil.toList("标题", "内容", "主题", "是否推荐",
                    "链接", "是否精确", "是否需要润色", "创建时间", "创建人", "部门");
            rows.add(titleList);
            if (CollectionUtils.isNotEmpty(list)) {

                List<KnowledgeDataType> categoryList = typeService.list();
                Map<Long, KnowledgeDataType> categoryMap = categoryList.stream().collect(Collectors.toMap(
                        KnowledgeDataType::getId,
                        p -> p,
                        (k1, k2) -> k1,
                        Maps::newHashMap
                ));

                list.forEach(item -> {
                    String categoryName = getCategoryName(item.getCategory(), categoryMap);
                    List<String> dataList = ListUtil.toList(item.getTitle(), item.getContent(),
                            categoryName, item.getSuggest(), item.getLink(), item.getAccurate(),
                            item.getPolishFlag(), item.getCreateTime(), item.getCreateUser(),
                            item.getDeptName());
                    rows.add(dataList);
                });
            }

            EasyExcelUtil.export(rows, outputStream);
            file = new File(fileName);
        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        } finally {
            if (null != file) {
                FileUtil.del(file);
            }
        }
    }

    @Override
    public void deleteByTitle(String title, List<String> knowledgeIds) {
        if (StringUtils.isBlank(title) || CollectionUtil.isEmpty(knowledgeIds)) {
            log.warn("title or knowledgeIds is null");
            return;
        }
        LambdaEsUpdateWrapper<KnowledgeData> wrapper = EsWrappers.lambdaUpdate(KnowledgeData.class)
                .eq(KnowledgeData::getTitle, title)
                .in(KnowledgeData::getKnowledgeId, knowledgeIds);
        KnowledgeData knowledgeData = new KnowledgeData();
        knowledgeData.setDelStatus("1");
        dataMapper.update(knowledgeData, wrapper);
    }

    /**
     * 导入数据
     *
     * @param file
     * @param param
     * @throws IOException
     */
    private void importData(MultipartFile file, importKnowledgeDataParam param, TokenUser tokenUserInfo, String requestId) throws IOException {
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        // 第一行为  标题	内容	主题(一级/二级)	是否推荐(推荐/不推荐)	 链接	是否精确(是/否)   是否需要润色(是/否)
        // 从第二行开始读取
        int row = 1;
        List<List<Object>> read = reader.read();
        read = read.subList(row, read.size());
        int total = read.size();
        // 获取当前知识库下的分类
        Wrappers<Object> dataTypeWrappers = Wrappers.init()
                .where(KNOWLEDGE_DATA_TYPE.KNOWLEDGE_ID.eq(param.getKnowledgeId())).and(KNOWLEDGE_DATA_TYPE.DELETE_FLAG.eq(0));
        List<KnowledgeDataType> dataTypeList = typeService.list(dataTypeWrappers);
        Map<String, KnowledgeDataType> alltypeMap = initTypeTree(dataTypeList, null);
        // 获取根目录
        KnowledgeDataType rootDataType = alltypeMap.get(KnowledgeConstant.ALL);
        if (null == rootDataType) {
            // 根目录不存在则创建
            rootDataType = addAllKnowledgeType(param.getKnowledgeId());
            dataTypeList.add(rootDataType);
            alltypeMap = initTypeTree(dataTypeList, null);
        }

        Vector<KnowledgeData> dataList = new Vector<>();
        ThreadPoolExecutor poolExecutor = ExecutorBuilder.create().setCorePoolSize(8).setMaxPoolSize(8).setKeepAliveTime(0).setWorkQueue(new LinkedBlockingQueue<>()).build();
        String format = LocalDateTimeUtil.format(LocalDateTime.now(), DateUtil.DEFAULT_FORMAT);
        AtomicInteger processedCount = new AtomicInteger(0);
        // websocket开关，避免websocket断开后重连一致推送
        Map<String, String> categoryMap = new HashMap<>();
        // 读取数据，解析每一行数据
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("开始解析excel表格QA数据");
        for (List<Object> dataDetailList : read) {
            if (dataDetailList.size() < 2 || null == dataDetailList.get(1) || StringUtils.isBlank(dataDetailList.get(1).toString())) {
                continue;
            }
            List<Object> objects = JSON.parseArray(JSON.toJSONString(dataDetailList));

            String categoryStr = getValue(objects, 2);

            String category = StringConstant.BLANK;
            // 如果根目录不存在，则excel中的主题列需必须有值才可以
            if (null == rootDataType) {
                if (StringUtils.isBlank(categoryStr)) {
                    throw new RuntimeException("根目录（目录名字为全部的目录）不存在，请在excel中的主题列补充对应目录名称");
                }
            } else {
                category = String.valueOf(rootDataType.getId());
            }
            if (StringUtils.isNotBlank(categoryStr)) {
                // 判断一级分类是否是全部，不是则需要手动添加
                if (!categoryStr.startsWith("全部")) {
                    categoryStr = "全部/" + categoryStr;
                }
                if (categoryMap.containsKey(categoryStr)) {
                    category =  categoryMap.get(categoryStr);
                } else {
                    // 如果主题不为空，则重新获取分类
                    category = dealCategory(categoryStr, alltypeMap, param.getKnowledgeId());
                    categoryMap.put(categoryStr, category);
                }
            }
            String finalCategory = category;
            KnowledgeData data = new KnowledgeData();
            data.setTitle(getValue(objects, 0));
            data.setContent(getValue(objects, 1));
            data.setCategory(finalCategory);
            data.setStatus(YesNoEnum.YES.getName());
            data.setSuggest(getValue(objects, 3));
            data.setLink(getValue(objects, 4));
            data.setAccurate(getValue(objects, 5));
            if (StringUtils.isBlank(data.getSuggest())) {
                data.setSuggest(KnowledgeConstant.RECOMMEND);
            }
            data.setUpdateTime(format);
            data.setKnowledgeId(param.getKnowledgeId());
            data.setPolishFlag(getValue(objects, 6));
            data.setDataSource(2 + "");
            if (tokenUserInfo != null) {
                data.setDeptId(tokenUserInfo.getDeptId());
                data.setDeptName(tokenUserInfo.getDeptName());
                data.setUserId(tokenUserInfo.getId() + "");
                data.setUserName(tokenUserInfo.getUserName());
                data.setCreateUser(tokenUserInfo.getUserName());
                data.setCreateTime(DateUtil.getCurrentDateString());
            }
            dataList.add(data);
        }
        log.info("-------------知识库数据导入------");
        stopWatch.stop();
        log.info(stopWatch.getLastTaskName() + "耗时: " + stopWatch.getTotalTimeSeconds());
        // 读取数据，解析每一行数据
        for (KnowledgeData knowledgeData : dataList) {
            poolExecutor.execute(() -> {
                denseVectorService.modelEncode(knowledgeData.getTitle(), param.getKnowledgeId(), knowledgeData, TITLE_DENSE_FILED);
                denseVectorService.modelEncode(knowledgeData.getTitle() + knowledgeData.getContent(), param.getKnowledgeId(), knowledgeData, CONTENT_DENSE_FILED);
                processedCount.incrementAndGet();
            });
        }

        // websocket通知进度
        JSONObject websocket = new JSONObject();
        websocket.put("nums", 0);
        websocket.put("msg", "");
        websocket.put("type", WebsocketTypeEnums.KNOWLEDGE_QA_PUSH);
        websocket.put("request_id", requestId);

        boolean isEmpty = CollectionUtil.isEmpty(read);
        TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
        String accountName = "admin";
        if (StringUtils.isNotBlank(tokenOauthUserInfo.getAccountName())) {
            accountName = tokenOauthUserInfo.getAccountName();
        }

        // 通知错误信息
        if (isEmpty) {
            websocket.put("msg", "导入失败，数据不能为空");
            WebSocketServer.sendInfo(JSON.toJSONString(websocket), accountName);
            poolExecutor.shutdown();
            return;
        }
        int initProcess = 5; // (分配创建文件夹+解析数据) 5%
        // 通知导入进度
        websocket.put("business", "知识库文件夹和解析数据");
        websocket.put("nums", initProcess);
        WebSocketServer.sendInfo(JSON.toJSONString(websocket), accountName);


        while (poolExecutor.getActiveCount() != 0) {
            ThreadUtil.safeSleep(1000);
            int currentProcessed = processedCount.get();
            // 向量化数据 65%
            double process = initProcess + (BigDecimal.valueOf(currentProcessed)
                    .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(65)).doubleValue());
            websocket.put("nums", process);
            WebSocketServer.sendInfo(JSON.toJSONString(websocket), accountName);
        }


        poolExecutor.shutdown();

//        websocket.put("business", "知识库数据正在保存");
//        websocket.put("nums", 0);

        // 保存数据
        if (CollectionUtil.isNotEmpty(dataList)) {

            boolean anyMatch = dataList.stream().anyMatch(p -> CollectionUtil.isEmpty(p.getContentDense()));
            if (anyMatch) {
                log.info("===>导入知识库数据失败，数据向量化失败");
            }
            // 通知导入进度
            stopWatch.start("开始保存数据QA数据，并推送进度");
            saveData(param.getReplaceFlag(), dataList, param.getKnowledgeId(), websocket, total, accountName);
            stopWatch.stop();
            log.info(stopWatch.getLastTaskName() + "耗时: " + stopWatch.getTotalTimeSeconds());
            String key = RedisKey.IMPORTING + (StringUtils.isNotBlank(accountName) ? accountName : "admin");
            redisService.del(key);
        }
        log.info("===>导入知识库数据完成，共导入：{}条数据", dataList.size());
    }

    /**
     * 从单元格获取数据
     *
     * @param dataList
     * @param index
     * @return
     */
    public static String getValue(List<Object> dataList, Integer index) {
        if (dataList.size() > index && null != dataList.get(index) && StringUtils.isNotBlank(dataList.get(index).toString())) {
            return dataList.get(index).toString();
        }
        return StringConstant.BLANK;
    }

    /**
     * 保存数据
     *
     * @param replaceFlag
     * @param dataList
     * @param knowledgeId
     * @param websocket
     * @param total
     * @param accountName
     * @throws IOException
     */
    private void saveData(boolean replaceFlag, Vector<KnowledgeData> dataList, String knowledgeId, JSONObject websocket, int total, String accountName) throws IOException {
        if (replaceFlag) {
            LambdaEsQueryWrapper<KnowledgeData> deleteWrapper = EsWrappers.lambdaQuery(KnowledgeData.class)
                    .eq(KnowledgeData::getKnowledgeId, knowledgeId);
            log.info("===>正在清理数据");
            dataMapper.delete(deleteWrapper);
            log.info("===>清理数据完成，正在保存数据");
        }
        List<KnowledgeData> toSaveList = ListUtil.toList();
        long pageNo = 1;
        int num = 0;
        int limit = 200;
        int initProcess = 70;
        while (true) {
            toSaveList = dataList.stream().skip((pageNo - 1) * limit).limit(limit).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(toSaveList)) {
                break;
            }
            dataMapper.insertBatch(toSaveList);
            num += toSaveList.size();
            pageNo++;
            double process = initProcess + BigDecimal.valueOf(num).divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(30)).doubleValue();
            websocket.put("nums", process);
            WebSocketServer.sendInfo(JSON.toJSONString(websocket), accountName);
        }
    }

    /**
     * 分类名称
     *
     * @param category
     * @param categoryMap
     * @return
     */
    private String getCategoryName(String category, Map<Long, KnowledgeDataType> categoryMap) {
        if (StringUtils.isBlank(category)) {
            return StringConstant.BLANK;
        }
        String[] split = category.split("/");
        return Arrays.stream(split).filter(StringUtils::isNotBlank).map(p -> categoryMap.get(Long.parseLong(p))).map(KnowledgeDataType::getType).collect(Collectors.joining("/"));
    }

    /**
     * 初始化分类树
     */
    private Map<String, KnowledgeDataType> initTypeTree(List<KnowledgeDataType> allTypes,
                                                        Map<String, KnowledgeDataType> alltypeMap) {
        Map<Long, List<KnowledgeDataType>> typeGroup = allTypes.stream().collect(Collectors.groupingBy(KnowledgeDataType::getPid));
        allTypes.forEach(item -> item.setChildren(typeGroup.get(item.getId())));
        alltypeMap = allTypes.stream()
                .filter(p -> p.getPid() == 0)
                .collect(Collectors.toMap(
                        KnowledgeDataType::getType,
                        p -> p,
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
        return alltypeMap;
    }

    /**
     * 处理分类
     *
     * @param categoryStr
     * @param alltypeMap
     * @return
     */
    private String dealCategory(String categoryStr, Map<String, KnowledgeDataType> alltypeMap, String knowledgeId) {
        // 获取分类，将分类名称改为分类id
        if (StringUtils.isNotBlank(categoryStr)) {
            // 获取分类，格式一般为分类1/分类2/分类3
            String[] split = categoryStr.split("/");
            // 遍历查询，如果不存在则自动创建
            Long pid = 0L;
            for (String s : split) {
                KnowledgeDataType entity = typeService.getOne(Wrappers.init()
                        .where(new QueryColumn("type").eq(s))
                        .and(new QueryColumn("knowledge_id").eq(knowledgeId))
                        .and(new QueryColumn("pid").eq(pid))
                        .and(new QueryColumn("status").eq(1)));
                if (entity == null) {
                    entity = new KnowledgeDataType();
                    entity.setType(s);
                    entity.setPid(pid);
                    entity.setKnowledgeId(knowledgeId);
                    typeService.save(entity);
                }
                // 更新alltypeMap
                Wrappers<Object> dataTypeWrappers = Wrappers.init()
                        .where(KNOWLEDGE_DATA_TYPE.KNOWLEDGE_ID.eq(knowledgeId)).and(KNOWLEDGE_DATA_TYPE.DELETE_FLAG.eq(0));
                List<KnowledgeDataType> dataTypeList = typeService.list(dataTypeWrappers);
                alltypeMap = initTypeTree(dataTypeList, alltypeMap);
                pid = entity.getId();
            }

            // 拼接分类
            StringBuilder mas = new StringBuilder();
            // 当前分类对象
            KnowledgeDataType knowledgeDataType = null;
            for (String str : split) {
                if (mas.length() == 0) {
                    knowledgeDataType = alltypeMap.get(str);
                } else if (null != knowledgeDataType) {
                    // 获取子级分类列表
                    List<KnowledgeDataType> children = knowledgeDataType.getChildren();
                    // 将当前分类置空，用来获取子级分类
                    knowledgeDataType = null;
                    if (CollectionUtils.isNotEmpty(children)) {
                        // 获取子级分类
                        Optional<KnowledgeDataType> any = children.stream().filter(p -> p.getType().equals(str)).findAny();
                        if (any.isPresent()) {
                            // 将当前分类对象设置为子级分类对象
                            knowledgeDataType = any.get();
                        }
                    }
                }

                // 拼接分类，使用/拼接
                if (null != knowledgeDataType) {
                    mas.append(knowledgeDataType.getId()).append("/");
                }
            }
            if (StringUtils.isNotBlank(mas)) {
                String categoryId = mas.substring(0, mas.length() - 1);
                return categoryId;
            }
        }
        return StringConstant.BLANK;
    }


    /**
     * 拼接分类id
     *
     * @param knowledgeDataTypes
     * @return
     */
    private List<String> getPaths(List<KnowledgeDataType> knowledgeDataTypes, String cateGoryParentIdStr) {
        List<String> paths = new ArrayList<>();
        for (KnowledgeDataType node : knowledgeDataTypes) {
            buildPath(node, cateGoryParentIdStr, paths);
        }

        if (CollectionUtils.isNotEmpty(paths)) {
            paths.remove(0);
        }

        return paths;
    }


    /**
     * 递归拼接子分类id
     *
     * @param node
     * @param currentPath
     * @param paths
     */
    private void buildPath(KnowledgeDataType node, String currentPath, List<String> paths) {
        if (node == null) {
            return;
        }

        currentPath = currentPath.isEmpty() ? node.getId().toString() : currentPath + "/" + node.getId();
        paths.add(currentPath);

        if (CollectionUtil.isNotEmpty(node.getChildren())) {
            for (KnowledgeDataType child : node.getChildren()) {
                buildPath(child, currentPath, paths);
            }
        }
    }

    @Override
    public List<List<KnowledgeDataScopeResult>> getKnowledgeDataScope(KnowledgeDataPageParam param) {
        param.setPageNo(1);
        param.setPageSize(9999);
        Result<EsPageInfo<KnowledgeDataResult>> knowledgeDataList = this.getKnowledgeDataList(param);
        List<KnowledgeDataResult> results= knowledgeDataList.getData().getList();
        // 对比出相似度的数据，无需参与重排比对
        List<String> notComparedIds = new ArrayList<>();
        List<List<KnowledgeDataScopeResult>> dataScopeResults = new ArrayList<>();
        Integer groupIndex = 0;
        for (KnowledgeDataResult firstResult : results) {
            List<KnowledgeDataResult> compareList = new ArrayList<>();
            // 已经比对过的数据
            if (notComparedIds.contains(firstResult.getId())) {
                continue;
            }
            groupIndex++;
            RearrangeParam rearrangeParam = new RearrangeParam();
            // 调用雅意重排能力
            RearrangeParam.Content rangeContent = new RearrangeParam.Content();
            rangeContent.setQuery(firstResult.getTitle());
            List<RearrangeParam.Articles> articlesList = new ArrayList<>();
            List<String> contentList = new ArrayList<>();
            for (KnowledgeDataResult secondResult : results) {
                // 已经比对过的数据
                if (notComparedIds.contains(secondResult.getId())) {
                    continue;
                }
                RearrangeParam.Articles articles = new RearrangeParam.Articles();
                // 向量库没有title，这里固定给一个值
                articles.setTitle(secondResult.getTitle());
                articles.setPara(secondResult.getTitle());
                articlesList.add(articles);
                contentList.add(secondResult.getId());
                compareList.add(secondResult);
            }
            rangeContent.setArticles(articlesList);
            Vector<StepEntity> contextList = new Vector<>();
            // 重排并获取前几条
            StepEntity rearrangeStep = new StepEntity();
            rearrangeStep.setStep(FINAL_COLLECT_RANGE);
            contextList.add(rearrangeStep);
            // 先重排
            Map<Integer, BigDecimal> rearrange = rearrange(rangeContent,
                    articlesList, rearrangeParam, contentList,
                    contextList,
                    param.getQaSimilarityScoreMin(),
                    param.getQaSimilarityScoreMax(),
                    notComparedIds);
            // 遍历 map 的 entrySet
            if (rearrange.entrySet().size() > 1) {
                List<KnowledgeDataScopeResult> groupScopeResults = new ArrayList<>();
                for (Map.Entry<Integer, BigDecimal> entry : rearrange.entrySet()) {
                    KnowledgeDataResult knowledgeDataResult = compareList.get(entry.getKey());
                    KnowledgeDataScopeResult knowledgeDataScopeResult = new KnowledgeDataScopeResult();
                    BeanUtils.copyProperties(knowledgeDataResult, knowledgeDataScopeResult);

                    knowledgeDataScopeResult.setGroupIndex(groupIndex);
                    knowledgeDataScopeResult.setSimilarityScores(entry.getValue().floatValue());
                    // 不需要和自己对比
//                    if (knowledgeDataResult.getId().equals(firstResult.getId())) {
//                        knowledgeDataScopeResult.setSimilarityScores(null);
//                    }
                    knowledgeDataScopeResult.setGroupIndex(groupIndex);
                    groupScopeResults.add(knowledgeDataScopeResult);
                }
                groupScopeResults.sort(Comparator.comparingDouble(KnowledgeDataScopeResult::getSimilarityScores).reversed());
                dataScopeResults.add(groupScopeResults);
            }
        }
        return dataScopeResults;
    }

    /**
     * 重排
     *
     * @param rangeContent
     * @param articlesList
     * @param rearrangeParam
     * @param contentList
     * @return
     */
    private Map<Integer, BigDecimal> rearrange(RearrangeParam.Content rangeContent,
                                   List<RearrangeParam.Articles> articlesList,
                                   RearrangeParam rearrangeParam,
                                   List<String> contentList,
                                   Vector<StepEntity> contextList,
                                   float qaSimilarityScoreMix,
                                   float qaSimilarityScoreMax,
                                   List<String> notComparedIds) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        Map<Integer, BigDecimal> indexScore = new HashMap<>();
        if (CollectionUtils.isEmpty(articlesList)) {
            return Maps.newHashMap();
        }
        // 重排并获取前几条
        StepEntity rearrangeStep = new StepEntity();
        rearrangeStep.setStep(FINAL_COLLECT_RANGE);
        contextList.add(rearrangeStep);
        rangeContent.setN(articlesList.size());
        rearrangeParam.setContent(rangeContent);
        // 调用重排能力
        rearrangeStep.setPrompt(rearrangeParam);
        RearrangeResult rearrange = answerUtils.buildRearrangeModel(rearrangeParam, applicationInfo);
        rearrangeStep.setPrompt(rearrangeParam);
        rearrangeStep.setResult(rearrange);
        RearrangeResult.RearrangeData data = rearrange.getData();

        // 获取重排后的索引
        List<Integer> indexList = data.getIndex_list();
        List<BigDecimal> resScoresList = data.getRes_scores_list();
        if (CollectionUtils.isNotEmpty(indexList)) {
            int size = contentList.size();
            size = Math.min(resScoresList.size(), size);
            if (size >= 2) {
                for (int i = 0; i < size; i++) {
                    BigDecimal score = resScoresList.get(i).setScale(2, RoundingMode.HALF_UP);
                    Integer index = indexList.get(i);
                    if (score.compareTo(BigDecimal.valueOf(qaSimilarityScoreMix)) >= 0
                            && score.compareTo(BigDecimal.valueOf(qaSimilarityScoreMax)) <= 0 ) {
                        indexScore.put(index, score);
                        notComparedIds.add(contentList.get(index));
                    }
                }
            }
        }
        return indexScore;
    }

    @Override
    public List<KnowledgeData> getKnowledgeData(List<String> knowledgeIds) {
        LambdaEsQueryWrapper<KnowledgeData> wrapper = EsWrappers.lambdaQuery(KnowledgeData.class)
                .select(KnowledgeData::getId,
                        KnowledgeData::getTitle,
                        KnowledgeData::getKnowledgeId,
                        KnowledgeData::getCategory,
                        KnowledgeData::getUpdateTime)
                .in(CollectionUtils.isNotEmpty(knowledgeIds), KnowledgeData::getKnowledgeId, knowledgeIds)
                .eq(KnowledgeData::getDelStatus, "0")
                .not(item -> item.isNotNull(KnowledgeData::getCategory))
                .orderByDesc(KnowledgeData::getUpdateTime)
                .limit(5000);
        return dataMapper.selectList(wrapper);
    }

    @Override
    public void updateKnowledgeData(List<KnowledgeData> knowledgeDataList) {
        dataMapper.updateBatchByIds(knowledgeDataList);
    }

    /**
     * 添加全部文件夹
     * @param knowledgeId
     * @return
     */
    private KnowledgeDataType addAllKnowledgeType(String knowledgeId) {
        // QA类型使用的不是Folders
        KnowledgeDataType knowledgeDataType = new KnowledgeDataType();
        knowledgeDataType.setType(KnowledgeConstant.ALL);
        knowledgeDataType.setKnowledgeId(knowledgeId);
        knowledgeDataType.setStatus(Integer.valueOf(YesNoEnum.YES.getCode()));
        knowledgeDataType.setPid(0L);
        typeService.addKnowledgeDataType(knowledgeDataType);

        return knowledgeDataType;
    }
}
