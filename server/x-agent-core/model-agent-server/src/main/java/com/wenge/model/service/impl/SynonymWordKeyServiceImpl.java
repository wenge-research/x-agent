package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSONObject;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.ImportSynonymWordDataParam;
import com.wenge.model.dto.param.SynonymWordPageParam;
import com.wenge.model.entity.SynonymWord;
import com.wenge.model.entity.SynonymWordKey;
import com.wenge.model.mapper.SynonymWordKeyMapper;
import com.wenge.model.mapper.SynonymWordMapper;
import com.wenge.model.service.MatterGuideFiledService;
import com.wenge.model.service.SynonymWordKeyService;
import com.wenge.model.utils.DateUtil;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.utils.WosUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.SynonymWordKeyTableDef.SYNONYM_WORD_KEY;
import static com.wenge.model.entity.table.SynonymWordTableDef.SYNONYM_WORD;

@Service
public class SynonymWordKeyServiceImpl extends ServiceImpl<SynonymWordKeyMapper, SynonymWordKey> implements SynonymWordKeyService {

    @Autowired
    private SynonymWordMapper synonymWordMapper;

    @Autowired
    private WosUtil wosUtil;

    @Value("${appframe.minio.bucket}")
    private String bucket;

    @Autowired
    private MatterGuideFiledService matterGuideFiledService;

    @Autowired
    private ResourceLoader resourceLoader;


    @Override
    @Transactional
    public Result addSynonymWordKey(SynonymWordKey synonymWordKey) {
        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        synonymWordKey.setCreateTime(DateUtil.getCurrentTime());
        synonymWordKey.setUpdateTime(synonymWordKey.getCreateTime());
        synonymWordKey.setCreateUserId(tokenUserInfo.getId() + "");
        synonymWordKey.setCreateUserName(tokenUserInfo.getUserName());
        synonymWordKey.setCreateUserAccount(tokenUserInfo.getAccountName());
        synonymWordKey.setId(IdUtil.simpleUUID());
        synonymWordKey.setDeletedFlag(0);
        synonymWordKey.setSource(0);
        // 查重
        if (checkRepeat(synonymWordKey)) {
            return Result.fail("当前词名称已存在");
        }
        List<SynonymWord> synonymWordList = synonymWordKey.getSynonymWordList();
        List<SynonymWord> synonymWords = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(synonymWordList)) {
            synonymWordList.forEach(synonymWord -> {
                synonymWord.setSynonymWordKeyId(synonymWordKey.getId());
                synonymWords.add(synonymWord);
            });
            synonymWordMapper.insertBatch(synonymWords);
        }
        synonymWordKey.setWordCount(synonymWords.size());
        mapper.insert(synonymWordKey);
        return Result.success(synonymWordKey);
    }

    /**
     * 检查关键字是否重复
     * @param synonymWordKey
     * @return
     */
    private boolean checkRepeat(SynonymWordKey synonymWordKey) {

        QueryWrapper queryWrapper = Wrappers.init()
                .where(SYNONYM_WORD_KEY.KEY_WORD.eq(synonymWordKey.getKeyWord()))
                .and(SYNONYM_WORD_KEY.DELETED_FLAG.eq(0));
        if (synonymWordKey.getId() != null) {
            queryWrapper.and(SYNONYM_WORD_KEY.ID.ne(synonymWordKey.getId()));
        }
        queryWrapper.limit(1);
        if (null != getOne(queryWrapper)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    @Transactional
    public Result editSynonymWord(SynonymWordKey synonymWordKey) {

        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        synonymWordKey.setUpdateTime(DateUtil.getCurrentTime());
        synonymWordKey.setUpdateUserId(tokenUserInfo.getId() + "");
        //查重
        if (checkRepeat(synonymWordKey)) {
            return Result.fail("当前词名称已存在");
        }
        List<SynonymWord> synonymWordList = synonymWordKey.getSynonymWordList();
        List<SynonymWord> synonymWords = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(synonymWordList)) {
            // 删除旧的关联关系
            QueryWrapper queryWrapper = Wrappers.create()
                    .where(SYNONYM_WORD.SYNONYM_WORD_KEY_ID.eq(synonymWordKey.getId()));
            synonymWordMapper.deleteByQuery(queryWrapper);
            // 重新绑定关系
            synonymWordList.forEach(synonymWord -> {
                synonymWord.setSynonymWordKeyId(synonymWordKey.getId());
                synonymWords.add(synonymWord);
            });
            synonymWordMapper.insertBatch(synonymWords);
        }
        synonymWordKey.setWordCount(synonymWords.size());
        updateById(synonymWordKey);
        return Result.success(synonymWordKey);
    }

    @Override
    public Result deleteSynonymWords(List<String> idList) {
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        SynonymWordKey synonymWordKey = new SynonymWordKey();
        synonymWordKey.setDeletedFlag(1);
        synonymWordKey.setUpdateTime(DateUtil.getCurrentTime());
        synonymWordKey.setUpdateUserId(tokenUserInfo.getId() + "");
        boolean update = update(synonymWordKey, Wrappers.init().where(SYNONYM_WORD_KEY.ID.in(idList)));
        return Result.success(update);
    }

    @Override
    public Result queryDetails(SynonymWordKey synonymWord) {
        SynonymWordKey synonymWordKey = this.getById(synonymWord.getId());
        if (ObjectUtils.isNotEmpty(synonymWordKey)) {
            QueryWrapper queryWrapper = Wrappers.create()
                    .where(SYNONYM_WORD.SYNONYM_WORD_KEY_ID.eq(synonymWordKey.getId()));
            List<SynonymWord> synonymWords = synonymWordMapper.selectListByQuery(queryWrapper);
            synonymWordKey.setSynonymWordList(synonymWords);

            return Result.success(synonymWordKey);
        }
        return Result.fail("同义词不存在");
    }

    @Override
    public Result queryList(SynonymWordPageParam synonymWordPageParam) {
        QueryWrapper wrappers = Wrappers.init()
                .where(SYNONYM_WORD_KEY.DELETED_FLAG.eq(0))
                .and(StringUtils.isNotBlank(synonymWordPageParam.getType()), SYNONYM_WORD_KEY.TYPE.eq(synonymWordPageParam.getType()))
                .and(synonymWordPageParam.getId() != null, SYNONYM_WORD_KEY.ID.eq(synonymWordPageParam.getId()))
                .and(StringUtils.isNotBlank(synonymWordPageParam.getKeyword()), SYNONYM_WORD_KEY.KEY_WORD.like(synonymWordPageParam.getKeyword()))
                .and(StringUtils.isNotBlank(synonymWordPageParam.getStartUpdateTime()), SYNONYM_WORD_KEY.UPDATE_TIME.ge(synonymWordPageParam.getStartUpdateTime()))
                .and(StringUtils.isNotBlank(synonymWordPageParam.getEndUpdateTime()), SYNONYM_WORD_KEY.UPDATE_TIME.le(synonymWordPageParam.getEndUpdateTime()));
        Page<SynonymWordKey> page = page(Page.of(synonymWordPageParam.getPageNo(), synonymWordPageParam.getPageSize()), wrappers);
        page.getRecords().forEach(record -> {
            QueryWrapper queryWrapper = Wrappers.create()
                    .where(SYNONYM_WORD.SYNONYM_WORD_KEY_ID.eq(record.getId()));
            List<SynonymWord> synonymWords = synonymWordMapper.selectListByQuery(queryWrapper);
            record.setSynonymWordList(synonymWords);
        });
        return Result.success(page);
    }

    @Override
    public Result querySelect() {
        Wrappers<Object> queryWrapper = Wrappers.init()
                .select(SYNONYM_WORD_KEY.TYPE)
                .groupBy(SYNONYM_WORD_KEY.TYPE);
        List<String> deptCountDtoList = mapper.selectListByQueryAs(queryWrapper, String.class);
        return Result.success(deptCountDtoList);
    }

    @Override
    public List<SynonymWord> queryBySynonymWord(String keyword) {
        QueryWrapper queryWrapper = Wrappers.create()
                .where(SYNONYM_WORD.CONTENT.eq(keyword));
        return synonymWordMapper.selectListByQuery(queryWrapper);
    }

    @Override
    public Result<String> importSynonymWordData(ImportSynonymWordDataParam param) {
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        MultipartFile file = param.getFile();
        if (file == null) {
            return Result.fail("文件不能为空");
        } else if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")) {
            return Result.fail("不支持该文件类型，请上传xlsx类型的文件");
        }
        final String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //上传文件
        final MinioInfoResult minioInfoResult = matterGuideFiledService.uploadFile(file,
                System.currentTimeMillis() + "." + suffix);
        //读取数据
        List<List<Object>> lists = null;
        try {
            lists = readerFile(wosUtil.getInput(bucket,minioInfoResult.getFileStoreKey() + "/" + minioInfoResult.getFileName()));
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail("文件解析异常，请检查重试");
        }
        //开始导入数据 成功多少条 失败多少条
        return insertData(lists, tokenUserInfo);
    }

    /**
     * @description: 导入数据导词库
     * @author: caohaifeng
     * @date: 2024/9/10 15:04
     **/
    private Result insertData(List<List<Object>> lists, TokenUser tokenUserInfo) {
        if (CollectionUtil.isEmpty(lists)) {
            return Result.fail("没有解析导需要导入的数据");
        }
        // 模版表头 : 关键词 所属分类 同义词[多个之间使用｜隔开]
        List<SynonymWordKey> correctWordList = mapper.selectAll();
        Map<String, SynonymWordKey> stringSynonymWordKeyMap = correctWordList.stream()
                .collect(Collectors.toMap(
                        SynonymWordKey::getKeyWord,
                        word -> word,
                        (existing, replacement) -> existing
                ));

        List<SynonymWordKey> synonymWordKeys = new ArrayList<>();
        List<String> correctWordKeyIds = new ArrayList<>();
        List<SynonymWord> correctWords = new ArrayList<>();
        for (List<Object> list : lists) {
            String currentTime = DateUtil.getCurrentTime();
            String keyWord = getValue(list, 0);
            String type = getValue(list, 1);
            String correctWordStr = getValue(list, 2);
            if (StringUtils.isEmpty(keyWord) || StringUtils.isEmpty(type) || StringUtils.isEmpty(correctWordStr)) {
                return Result.fail("表格数据不能为空");
            }
            SynonymWordKey synonymWordKey = stringSynonymWordKeyMap.get(keyWord);
            if (ObjectUtils.isEmpty(synonymWordKey)) {
                synonymWordKey = new SynonymWordKey();
                synonymWordKey.setCreateTime(currentTime);
                synonymWordKey.setUpdateTime(currentTime);
                synonymWordKey.setUpdateUserId(tokenUserInfo.getId() + "");
                synonymWordKey.setCreateUserId(tokenUserInfo.getId() + "");
                synonymWordKey.setCreateUserName(tokenUserInfo.getUserName());
                synonymWordKey.setCreateUserAccount(tokenUserInfo.getAccountName());
                synonymWordKey.setId(IdUtil.simpleUUID());
                synonymWordKey.setDeletedFlag(0);
                synonymWordKey.setType(type);
                synonymWordKey.setSource(1);
                synonymWordKey.setKeyWord(keyWord);
            } else {
                synonymWordKey.setUpdateTime(currentTime);
                synonymWordKey.setUpdateUserId(tokenUserInfo.getId() + "");
            }
            String[] split = correctWordStr.split("\\|");
            List<SynonymWord> synonymWordListOld = synonymWordKey.getSynonymWordList();
            for (String s : split) {
                if (CollectionUtil.isEmpty(synonymWordListOld)) {
                    synonymWordListOld = new ArrayList<>();
                }
                SynonymWord synonymWord = new SynonymWord();
                synonymWord.setSynonymWordKeyId(synonymWordKey.getId());
                synonymWord.setContent(s);
                synonymWordListOld.add(synonymWord);
            }
            synonymWordKey.setSynonymWordList(synonymWordListOld);
            synonymWordKey.setWordCount(synonymWordListOld.size());

            correctWordKeyIds.add(synonymWordKey.getId());
            correctWords.addAll(synonymWordKey.getSynonymWordList());
            saveOrUpdate(synonymWordKey);
        }

        // 删除旧的关联关系
        QueryWrapper queryWrapper = Wrappers.create()
                .where(SYNONYM_WORD.SYNONYM_WORD_KEY_ID.in(correctWordKeyIds));
        synonymWordMapper.deleteByQuery(queryWrapper);
        synonymWordMapper.insertBatch(correctWords);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", synonymWordKeys.size());
        jsonObject.put("msg", "导入成功");
        return Result.success(jsonObject);
    }

    private String getValue(List<Object> list, int index) {
        if (list.size() > index) {
            return list.get(index).toString();
        }
        return StringConstant.BLANK;
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
        return read;
    }

    @Override
    public void downloadSynonymWordDataTemp(HttpServletResponse response) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            Resource resource = resourceLoader.getResource("classpath:temp/synonymWordDataTemp.xlsx");
            inputStream = resource.getInputStream();

            // 设置响应头
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            // 获取文件名，并进行UTF-8编码
            String fileName = URLEncoder.encode("同义词导入模板.xlsx", StandardCharsets.UTF_8.toString());
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
}
