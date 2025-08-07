package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.ImportInterceptWordDataParam;
import com.wenge.model.dto.param.InterceptWordPageParam;
import com.wenge.model.entity.InterceptWord;
import com.wenge.model.entity.InterceptWordFile;
import com.wenge.model.entity.InterceptWordHouse;
import com.wenge.model.entity.InterceptWordType;
import com.wenge.model.entity.table.InterceptWordTableDef;
import com.wenge.model.event.SceneEvent;
import com.wenge.model.enums.InterceptTypeEnum;
import com.wenge.model.enums.StatusTypeEnum;
import com.wenge.model.mapper.*;
import com.wenge.model.service.InterceptWordHouseService;
import com.wenge.model.service.InterceptWordService;
import com.wenge.model.service.MatterGuideFiledService;
import com.wenge.model.utils.DateUtil;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.utils.WosUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.wenge.model.entity.table.InterceptWordHouseTableDef.INTERCEPT_WORD_HOUSE;
import static com.wenge.model.entity.table.InterceptWordTableDef.INTERCEPT_WORD;
import static com.wenge.model.entity.table.InterceptWordTypeTableDef.INTERCEPT_WORD_TYPE;

/**
 * Description: 拦截词服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-09 21:15:57
 *
 */
@Service
@Slf4j
public class InterceptWordServiceImpl extends ServiceImpl<InterceptWordMapper, InterceptWord> implements InterceptWordService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private InterceptWordMapper interceptWordMapper;

    @Autowired
    private InterceptWordHouseService interceptWordHouseService;

    @Autowired
    private InterceptWordHouseMapper interceptWordHouseMapper;

    @Autowired
    private MatterGuideFiledService matterGuideFiledService;

    @Autowired
    private InterceptWordFileMapper interceptWordFileMapper;


    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private WosUtil wosUtil;

    @Value("${appframe.minio.bucket}")
    private String bucket;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private InterceptWordTypeMapper interceptWordTypeMapper;

    @Autowired
    private InterceptWordHandlingMethodMapper interceptWordHandlingMethodMapper;

    @Override
    public Result addInterceptWord(InterceptWord interceptWord){
        publisher.publishEvent(new SceneEvent(interceptWord));
        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        interceptWord.setCreateTime(DateUtil.getCurrentTime());
        interceptWord.setUpdateTime(interceptWord.getCreateTime());
        interceptWord.setCreateUserId(tokenUserInfo.getId() + "");
        interceptWord.setCreateUserName(tokenUserInfo.getUserName());

        //查重
        if (checkRepeat(interceptWord)) {
            return Result.fail("当前词名称已存在");
        }
        save(interceptWord);
        //更新词库数量
        updateInterceptWordData(interceptWord.getInterceptWordHouseId());
        return Result.success();
    }

    @Override
    public Result getInterceptWordList(InterceptWordPageParam interceptWord){
        Wrappers wrappers = Wrappers.init()
                .where(INTERCEPT_WORD.DELETED_FLAG.eq(0))
                .and(StringUtils.isNotBlank(interceptWord.getType()), INTERCEPT_WORD.TYPE.eq(interceptWord.getType()))
                .and(StringUtils.isNotBlank(interceptWord.getStatus()), INTERCEPT_WORD.STATUS.eq(interceptWord.getStatus()))
                .and(interceptWord.getInterceptWordHouseId() != null, INTERCEPT_WORD.INTERCEPT_WORD_HOUSE_ID.eq(interceptWord.getInterceptWordHouseId()))
                .and(StringUtils.isNotBlank(interceptWord.getKeyword()), INTERCEPT_WORD.CONTENT.like(interceptWord.getKeyword()))
                .and(StringUtils.isNotBlank(interceptWord.getStartCreateTime()), INTERCEPT_WORD.CREATE_TIME.ge(interceptWord.getStartCreateTime()))
                .and(StringUtils.isNotBlank(interceptWord.getEndCreateTime()), INTERCEPT_WORD.CREATE_TIME.le(interceptWord.getEndCreateTime()));
        Page<InterceptWord> page = page(Page.of(interceptWord.getPageNo(), interceptWord.getPageSize()), wrappers);
        return Result.success(page);
    }

    @Override
    public List<InterceptWord> getInterceptWordListByHouseName(String interceptWordHouseName){
        // 敏感词拦截库名称唯一
        Wrappers houseWrappers = Wrappers.init()
                .where(INTERCEPT_WORD_HOUSE.DELETED_FLAG.eq(0))
                .and(INTERCEPT_WORD_HOUSE.STATUS.eq(StatusTypeEnum.ENABLE.getIntType()))
                .and(StringUtils.isNotBlank(interceptWordHouseName), INTERCEPT_WORD_HOUSE.NAME.eq(interceptWordHouseName))
                .limit(1);
        InterceptWordHouse interceptWordHouse = interceptWordHouseMapper.selectOneByQuery(houseWrappers);
        if (interceptWordHouse == null) {
            return new ArrayList<>();
        }

        long hourseId = interceptWordHouse.getId();
        Wrappers wordWrappers = Wrappers.init()
                .where(INTERCEPT_WORD.DELETED_FLAG.eq(0))
                .and(INTERCEPT_WORD.TYPE.in(InterceptTypeEnum.FORBIDDEN_WORD.getName(), InterceptTypeEnum.INTERCEPT_QUESTION.getName(),
                        InterceptTypeEnum.INTERCEPT_ALL_QUESTION.getName()))
                .and(INTERCEPT_WORD.STATUS.eq(StatusTypeEnum.ENABLE.getStrType()))
                .and(INTERCEPT_WORD.INTERCEPT_WORD_HOUSE_ID.eq(hourseId));

        return list(wordWrappers);
    }

    @Override
    public Result updateInterceptWord(InterceptWord interceptWord){
        publisher.publishEvent(new SceneEvent(interceptWord));
        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        interceptWord.setUpdateTime(DateUtil.getCurrentTime());
        interceptWord.setUpdateUserId(tokenUserInfo.getId() + "");
        //查重
        if (checkRepeat(interceptWord)) {
            return Result.fail("当前词名称已存在");
        }

        updateById(interceptWord);
        // 修改敏感词后，需要修改拦截词库时间
        if (null != interceptWord.getInterceptWordHouseId()) {
            InterceptWordHouse interceptWordHouseUpdate = InterceptWordHouse.builder()
                    .id(interceptWord.getInterceptWordHouseId())
                    .updateTime(DateUtil.getCurrentTime())
                    .updateUserId(tokenUserInfo.getId().toString())
                    .build();
            interceptWordHouseMapper.update(interceptWordHouseUpdate);
        }

        return Result.success();
    }

    @Override
    public Result deleteInterceptWord(List<String> idList){
        publisher.publishEvent(new SceneEvent(null));
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        InterceptWord interceptWord = InterceptWord.create();
        interceptWord.setDeletedFlag(1);
        interceptWord.setUpdateTime(DateUtil.getCurrentTime());
        interceptWord.setUpdateUserId(tokenUserInfo.getId() + "");
        final boolean update = update(interceptWord, Wrappers.init().where(INTERCEPT_WORD.ID.in(idList)));
        try {
            Long interceptWordHouseId = null;
            if (update) {
                interceptWordHouseId = getById(idList.get(0)).getInterceptWordHouseId();
            }
            //更新词库数量
            updateInterceptWordData(interceptWordHouseId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.success(update);
    }

    @Override
    public Result updateStatus(InterceptWord interceptWord) {
        publisher.publishEvent(new SceneEvent(interceptWord));
        InterceptWord interceptWordUpdate = InterceptWord.create();
        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        String currentTime = DateUtil.getCurrentTime();
        interceptWordUpdate.setId(interceptWord.getId());
        interceptWordUpdate.setStatus(interceptWord.getStatus());
        interceptWordUpdate.setUpdateTime(currentTime);
        interceptWordUpdate.setUpdateUserId(tokenUserInfo.getId() + "");

        // 修改敏感词后，需要修改拦截词库时间
        if (null != interceptWord.getInterceptWordHouseId()) {
            InterceptWordHouse interceptWordHouseUpdate = InterceptWordHouse.builder()
                    .id(interceptWord.getInterceptWordHouseId())
                    .updateTime(currentTime)
                    .updateUserId(tokenUserInfo.getId().toString())
                    .build();
            interceptWordHouseMapper.update(interceptWordHouseUpdate);
        }

        return Result.success(updateById(interceptWordUpdate));
    }

    @Override
    public Result getInterceptWordTypeList() {
        List<InterceptWordType> interceptWordTypes = interceptWordTypeMapper.selectListByQuery(Wrappers.init().where(INTERCEPT_WORD_TYPE.STATUS.eq(0)).limit(100));
        return Result.success(interceptWordTypes);
    }

    @Override
    public Result getInterceptWordHandlingMethodList() {
        return Result.success(interceptWordHandlingMethodMapper.selectListByQuery(Wrappers.init().limit(100)));
    }

    @Override
    public Result importInterceptWordData(ImportInterceptWordDataParam param) {
        Wrappers<Object> wrappers = Wrappers.init()
                .where(INTERCEPT_WORD_HOUSE.ID.eq(param.getInterceptWordHouseId()))
                .limit(1);
        InterceptWordHouse interceptWordHouse = interceptWordHouseService.getOne(wrappers);
        if (null == interceptWordHouse)  return Result.fail("当前敏感词库不存在");
        MultipartFile file = param.getFile();
        if (file == null) {
            return Result.fail("文件不能为空");
        } else if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")) {
            return Result.fail("不支持该文件类型，请上传xlsx类型的文件");
        }
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        final String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //上传文件
        final MinioInfoResult minioInfoResult = matterGuideFiledService.uploadFile(file,
                System.currentTimeMillis() + "." + suffix);
        if (minioInfoResult == null) {
            return Result.fail("文件上传失败，请重新导入");
        }
        InterceptWordFile interceptWordFile = getInterceptWordFile(file);
        interceptWordFile.setFileUrl(minioInfoResult.getUrlPath());
        interceptWordFile.setCreateUserId(tokenUserInfo.getId() + "");
        interceptWordFile.setCreateTime(DateUtil.getCurrentTime());
        interceptWordFileMapper.insert(interceptWordFile);
        log.info("文件上传记录落库成功...开始解析文件...");

        //读取数据
        List<List<Object>> lists = null;
        InterceptWordFile interceptWordFileUpdate = InterceptWordFile.create();
        interceptWordFileUpdate.setId(interceptWordFile.getId());
        try {
            lists = readerFile(wosUtil.getInput(bucket,minioInfoResult.getFileStoreKey() + "/" + minioInfoResult.getFileName()));
            interceptWordFileUpdate.setStatus(3);
        }catch (Exception e){
            log.error("文件解析异常...");
            interceptWordFileUpdate.setStatus(2);
            e.printStackTrace();
            return Result.fail("文件解析异常，请检查重试");
        }finally {
            interceptWordFileUpdate.setUpdateTime(DateUtil.getCurrentTime());
            interceptWordFileUpdate.setUpdateUserId(tokenUserInfo.getId() + "");
            interceptWordFileMapper.update(interceptWordFileUpdate);
        }
        //开始导入数据 成功多少条 失败多少条
        Result result = insertData(lists, param.getInterceptWordHouseId(), tokenUserInfo, interceptWordFile.getFileId(), interceptWordFile.getId());
        return result;
    }

    @Override
    public long updateInterceptWordData(Long interceptWordHouseId) {
        if (interceptWordHouseId == null) {
            return -1L;
        }
        QueryWrapper queryWrapper = Wrappers.init()
                .where(INTERCEPT_WORD.INTERCEPT_WORD_HOUSE_ID.eq(interceptWordHouseId))
                .and(INTERCEPT_WORD.DELETED_FLAG.eq(0));
        long count = interceptWordMapper.selectCountByQuery(queryWrapper);
        InterceptWordHouse interceptWordHouse = InterceptWordHouse.builder()
                .wordCount(Integer.valueOf(String.valueOf(count)))
                .id(interceptWordHouseId)
                .build();
        interceptWordHouseService.updateById(interceptWordHouse);
        return count;
    }


    /**
     * @description: 导入数据导词库
     * @author: caohaifeng
     * @date: 2024/9/10 15:04
     **/
    private Result insertData(List<List<Object>> lists, Long interceptWordHouseId, TokenUser tokenUserInfo, String fileId, Long id) {
        if (CollectionUtil.isEmpty(lists)) {
            return Result.fail("没有解析导需要导入的数据");
        }
        //模版表头 : 关键词 描述	分类	处理方式	输入限定答案	输入前缀	输入后缀	输入替换
//		final List<List<Object>> lists1 = lists.subList(1, lists.size());
        List<InterceptWord> interceptWordList = new ArrayList<>();
        lists.forEach(list -> {
            InterceptWord interceptWord = InterceptWord.create();
            interceptWord.setContent(getValue(list, 0));
            interceptWord.setRemark(getValue(list, 1));
            interceptWord.setType(getValue(list, 2));
            //处理方式
            Map<String, Object> processingMap = new HashMap<>();
            processingMap.put("way", getValue(list, 3));
            processingMap.put("answer", getValue(list, 4));
            processingMap.put("preQuestion", getValue(list, 5));
            processingMap.put("extendQuestion", getValue(list, 6));
            processingMap.put("replaceQuestion", getValue(list, 7));
            interceptWord.setProcessing(JSON.toJSONString(processingMap));

            //基础信息
            interceptWord.setCreateUserId(tokenUserInfo.getId() + "");
            interceptWord.setUpdateUserId(tokenUserInfo.getId() + "");
            String currentTime = DateUtil.getCurrentTime();
            interceptWord.setUpdateTime(currentTime);
            interceptWord.setCreateTime(currentTime);
            interceptWord.setCreateUserName(tokenUserInfo.getUserName());
            interceptWord.setCreateUserName(tokenUserInfo.getUserName());

            interceptWord.setInterceptWordHouseId(interceptWordHouseId);
            interceptWord.setExportFileId(fileId);
            interceptWord.setStatus("1");
            interceptWord.setDeletedFlag(0);
            interceptWord.setSource(1);
            interceptWordList.add(interceptWord);
        });
        boolean saveRes = saveBatch(interceptWordList);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", interceptWordList.size());
        jsonObject.put("msg", "导入失败");
        if (saveRes) {
            InterceptWordFile interceptWordFileUpdate = InterceptWordFile.create();
            interceptWordFileUpdate.setId(id);
            interceptWordFileUpdate.setUpdateTime(DateUtil.getCurrentTime());
            interceptWordFileUpdate.setUpdateUserId(tokenUserInfo.getId() + "");
            interceptWordFileUpdate.setStatus(4);
            interceptWordFileMapper.update(interceptWordFileUpdate);
            log.info("导入成功");
            jsonObject.put("msg", "导入成功");
        }
        //更新词库数量
        updateInterceptWordData(interceptWordHouseId);
        publisher.publishEvent(new SceneEvent(null));
        return Result.success(jsonObject);
    }

    /**
     * @description: 读取文件做解析
     * @author: caohaifeng
     * @date: 2024/9/10 15:04
     **/
    private List<List<Object>> readerFile(InputStream fileInput) {
        ExcelReader reader = ExcelUtil.getReader(fileInput);
        int row = 1;
        List<List<Object>> read = reader.read();
        read = read.subList(row, read.size());
        int total = read.size();
        return read;
    }

    @Override
    public void downloadInterceptWordDataTemp(HttpServletResponse response) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            Resource resource = resourceLoader.getResource("classpath:temp/interceptWordDataTemp.xlsx");
            inputStream = resource.getInputStream();

            // 设置响应头
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            // 获取文件名，并进行UTF-8编码
            String fileName = URLEncoder.encode("敏感词库导入模板.xlsx", StandardCharsets.UTF_8.toString());
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
    public List<InterceptWord> getAllWord() {
        Wrappers<Object> wrappers = Wrappers.init()
                .where(InterceptWordTableDef.INTERCEPT_WORD.STATUS.eq(StringConstant.ONE));
        return list(wrappers);
    }

    @Override
    public List<InterceptWord> getInterceptWordListByHouseIds(List<Long> houseIds) {
        if (CollectionUtil.isEmpty(houseIds)) {
            return Collections.emptyList();
        }
        return Wrappers.of(mapper).where(INTERCEPT_WORD.INTERCEPT_WORD_HOUSE_ID.in(houseIds))
                .and(INTERCEPT_WORD.DELETED_FLAG.eq(0)).and(INTERCEPT_WORD.STATUS.eq(StringConstant.ONE)).list();
    }



    private boolean checkRepeat(InterceptWord interceptWord) {
        QueryWrapper queryWrapper = Wrappers.init()
                .where(INTERCEPT_WORD.CONTENT.eq(interceptWord.getContent()))
                .and(INTERCEPT_WORD.INTERCEPT_WORD_HOUSE_ID.eq(interceptWord.getInterceptWordHouseId()))
                .and(INTERCEPT_WORD.DELETED_FLAG.eq(0));
        if (interceptWord.getId() != null) {
            queryWrapper.and(INTERCEPT_WORD.ID.ne(interceptWord.getId()));
        }
        queryWrapper.limit(1);
        if (null != getOne(queryWrapper)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private InterceptWordFile getInterceptWordFile(MultipartFile file) {
        InterceptWordFile interceptWordFile = InterceptWordFile.create();
        interceptWordFile.setFileId(IdUtil.simpleUUID());
        interceptWordFile.setFileName(file.getOriginalFilename());
        int indexOf = file.getOriginalFilename().lastIndexOf(".");
        String type = file.getOriginalFilename().substring(indexOf + 1);
        interceptWordFile.setFileType(type);
        interceptWordFile.setFileSize(file.getSize());
        interceptWordFile.setMultipartFile(file);
        interceptWordFile.setStatus(1);
        interceptWordFile.setDeletedFlag(0+"");
        return interceptWordFile;
    }

    private String getValue(List<Object> list, int index) {
        if (list.size() > index) {
            return list.get(index).toString();
        }
        return StringConstant.BLANK;
    }

}