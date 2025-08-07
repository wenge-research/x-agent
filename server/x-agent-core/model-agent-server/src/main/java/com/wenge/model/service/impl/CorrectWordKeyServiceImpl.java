package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSONObject;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.CorrectWordPageParam;
import com.wenge.model.dto.param.ImportCorrectWordDataParam;
import com.wenge.model.entity.CorrectWord;
import com.wenge.model.entity.CorrectWordKey;
import com.wenge.model.mapper.CorrectWordKeyMapper;
import com.wenge.model.mapper.CorrectWordMapper;
import com.wenge.model.service.CorrectWordKeyService;
import com.wenge.model.service.MatterGuideFiledService;
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

import static com.wenge.model.entity.table.CorrectWordKeyTableDef.CORRECT_WORD_KEY;
import static com.wenge.model.entity.table.CorrectWordTableDef.CORRECT_WORD;
import static com.wenge.model.entity.table.SynonymWordTableDef.SYNONYM_WORD;

@Service
public class CorrectWordKeyServiceImpl extends ServiceImpl<CorrectWordKeyMapper, CorrectWordKey> implements CorrectWordKeyService {

    @Autowired
    private CorrectWordMapper correctWordMapper;

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
    public Result addCorrectWord(CorrectWordKey correctWordKey) {
        // 获取当前用户信息
        String currentTime = DateUtil.getCurrentTime();
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        correctWordKey.setCreateTime(currentTime);
        correctWordKey.setUpdateTime(currentTime);
        correctWordKey.setCreateUserId(tokenUserInfo.getId() + "");
        correctWordKey.setCreateUserName(tokenUserInfo.getUserName());
        correctWordKey.setCreateUserAccount(tokenUserInfo.getAccountName());
        correctWordKey.setId(IdUtil.simpleUUID());
        correctWordKey.setDeletedFlag(0);
        correctWordKey.setSource(0);
        // 查重
        if (checkRepeat(correctWordKey)) {
            return Result.fail("当前词名称已存在");
        }
        List<CorrectWord> correctWordList = correctWordKey.getCorrectWordList();
        List<CorrectWord> synonymWords = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(correctWordList)) {
            correctWordList.forEach(synonymWord -> {
                synonymWord.setCorrectWordKeyId(correctWordKey.getId());
                synonymWords.add(synonymWord);
            });
            correctWordMapper.insertBatch(synonymWords);
        }
        correctWordKey.setWordCount(synonymWords.size());
        mapper.insert(correctWordKey);
        return Result.success(correctWordKey);
    }

    /**
     * 检查关键字是否重复
     * @param correctWordKey
     * @return
     */
    private boolean checkRepeat(CorrectWordKey correctWordKey) {

        QueryWrapper queryWrapper = Wrappers.init()
                .where(CORRECT_WORD_KEY.KEY_WORD.eq(correctWordKey.getKeyWord()))
                .and(CORRECT_WORD_KEY.DELETED_FLAG.eq(0));
        if (correctWordKey.getId() != null) {
            queryWrapper.and(CORRECT_WORD_KEY.ID.ne(correctWordKey.getId()));
        }
        queryWrapper.limit(1);
        if (null != getOne(queryWrapper)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    @Transactional
    public Result editCorrectWord(CorrectWordKey correctWordKey) {

        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        correctWordKey.setUpdateTime(DateUtil.getCurrentTime());
        correctWordKey.setUpdateUserId(tokenUserInfo.getId() + "");
        //查重
        if (checkRepeat(correctWordKey)) {
            return Result.fail("当前词名称已存在");
        }
        List<CorrectWord> correctWordList = correctWordKey.getCorrectWordList();
        List<CorrectWord> correctWords = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(correctWordList)) {
            // 删除旧的关联关系
            QueryWrapper queryWrapper = Wrappers.create()
                    .where(CORRECT_WORD.CORRECT_WORD_KEY_ID.eq(correctWordKey.getId()));
            correctWordMapper.deleteByQuery(queryWrapper);
            // 重新绑定关系
            correctWordList.forEach(correctWord -> {
                correctWord.setCorrectWordKeyId(correctWordKey.getId());
                correctWords.add(correctWord);
            });
            correctWordMapper.insertBatch(correctWords);
        }
        correctWordKey.setWordCount(correctWords.size());
        updateById(correctWordKey);
        return Result.success(correctWordKey);
    }

    @Override
    public Result deleteCorrectWord(List<String> idList) {

        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        CorrectWordKey correctWordKey = new CorrectWordKey();
        correctWordKey.setDeletedFlag(1);
        correctWordKey.setUpdateTime(DateUtil.getCurrentTime());
        correctWordKey.setUpdateUserId(tokenUserInfo.getId() + "");
        boolean update = update(correctWordKey, Wrappers.init().where(CORRECT_WORD_KEY.ID.in(idList)));
        return Result.success(update);
    }

    @Override
    public Result queryDetails(CorrectWordKey correctWord) {
        CorrectWordKey correctWordKey = this.getById(correctWord.getId());
        if (ObjectUtils.isNotEmpty(correctWordKey)) {
            QueryWrapper queryWrapper = Wrappers.create()
                    .where(CORRECT_WORD.CORRECT_WORD_KEY_ID.eq(correctWordKey.getId()));
            List<CorrectWord> synonymWords = correctWordMapper.selectListByQuery(queryWrapper);
            correctWordKey.setCorrectWordList(synonymWords);

            return Result.success(correctWordKey);
        }
        return Result.fail("纠错词不存在");
    }

    @Override
    public Result queryList(CorrectWordPageParam correctWordPageParam) {
        QueryWrapper wrappers = Wrappers.init()
                .where(CORRECT_WORD_KEY.DELETED_FLAG.eq(0))
                .and(StringUtils.isNotBlank(correctWordPageParam.getType()), CORRECT_WORD_KEY.TYPE.eq(correctWordPageParam.getType()))
                .and(correctWordPageParam.getId() != null, CORRECT_WORD_KEY.ID.eq(correctWordPageParam.getId()))
                .and(StringUtils.isNotBlank(correctWordPageParam.getKeyword()), CORRECT_WORD_KEY.KEY_WORD.like(correctWordPageParam.getKeyword()))
                .and(StringUtils.isNotBlank(correctWordPageParam.getStartUpdateTime()), CORRECT_WORD_KEY.UPDATE_TIME.ge(correctWordPageParam.getStartUpdateTime()))
                .and(StringUtils.isNotBlank(correctWordPageParam.getEndUpdateTime()), CORRECT_WORD_KEY.UPDATE_TIME.le(correctWordPageParam.getEndUpdateTime()));
        Page<CorrectWordKey> page = page(Page.of(correctWordPageParam.getPageNo(), correctWordPageParam.getPageSize()), wrappers);
        page.getRecords().forEach(record -> {
            QueryWrapper queryWrapper = Wrappers.create()
                    .where(CORRECT_WORD.CORRECT_WORD_KEY_ID.eq(record.getId()));
            List<CorrectWord> synonymWords = correctWordMapper.selectListByQuery(queryWrapper);
            record.setCorrectWordList(synonymWords);
        });
        return Result.success(page);
    }

    @Override
    public Result querySelect() {
        Wrappers<Object> queryWrapper = Wrappers.init()
                .select(CORRECT_WORD_KEY.TYPE)
                .groupBy(CORRECT_WORD_KEY.TYPE);
        List<String> deptCountDtoList = mapper.selectListByQueryAs(queryWrapper, String.class);
        return Result.success(deptCountDtoList);
    }

    @Override
    public List<CorrectWord> queryBySynonymWord(String keyword) {
        QueryWrapper queryWrapper = Wrappers.create()
                .where(SYNONYM_WORD.CONTENT.eq(keyword));
        return correctWordMapper.selectListByQuery(queryWrapper);
    }

    @Override
    public Result<String> importCorrectWordData(ImportCorrectWordDataParam param) {
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
        List<CorrectWordKey> correctWordList = mapper.selectAll();
        Map<String, CorrectWordKey> collectWordKeyMap = correctWordList.stream()
                .collect(Collectors.toMap(
                        CorrectWordKey::getKeyWord,
                        word -> word,
                        (existing, replacement) -> existing
                ));

        List<CorrectWordKey> correctWordKeys = new ArrayList<>();
        List<String> correctWordKeyIds = new ArrayList<>();
        List<CorrectWord> correctWords = new ArrayList<>();
        for (List<Object> list : lists) {
            String currentTime = DateUtil.getCurrentTime();
            String keyWord = getValue(list, 0);
            String type = getValue(list, 1);
            String correctWordStr = getValue(list, 2);
            if (StringUtils.isEmpty(keyWord) || StringUtils.isEmpty(type) || StringUtils.isEmpty(correctWordStr)) {
                return Result.fail("表格数据不能为空");
            }
            CorrectWordKey correctWordKey = collectWordKeyMap.get(keyWord);
            if (ObjectUtils.isEmpty(correctWordKey)) {
                correctWordKey = new CorrectWordKey();
                correctWordKey.setCreateTime(currentTime);
                correctWordKey.setUpdateTime(currentTime);
                correctWordKey.setUpdateUserId(tokenUserInfo.getId() + "");
                correctWordKey.setCreateUserId(tokenUserInfo.getId() + "");
                correctWordKey.setCreateUserName(tokenUserInfo.getUserName());
                correctWordKey.setCreateUserAccount(tokenUserInfo.getAccountName());
                correctWordKey.setId(IdUtil.simpleUUID());
                correctWordKey.setDeletedFlag(0);
                correctWordKey.setType(type);
                correctWordKey.setSource(1);
                correctWordKey.setKeyWord(keyWord);
            } else {
                correctWordKey.setUpdateTime(currentTime);
                correctWordKey.setUpdateUserId(tokenUserInfo.getId() + "");
            }
            String[] split = correctWordStr.split("\\|");
            List<CorrectWord> correctWordListOld = correctWordKey.getCorrectWordList();
            for (String s : split) {
                if (CollectionUtil.isEmpty(correctWordListOld)) {
                    correctWordListOld = new ArrayList<>();
                }
                CorrectWord correctWord = new CorrectWord();
                correctWord.setCorrectWordKeyId(correctWordKey.getId());
                correctWord.setContent(s);
                correctWordListOld.add(correctWord);
            }
            correctWordKey.setCorrectWordList(correctWordListOld);
            correctWordKey.setWordCount(correctWordListOld.size());

            correctWordKeyIds.add(correctWordKey.getId());
            correctWords.addAll(correctWordKey.getCorrectWordList());

            saveOrUpdate(correctWordKey);
        }
        // 删除旧的关联关系
        QueryWrapper queryWrapper = Wrappers.create()
                .where(CORRECT_WORD.CORRECT_WORD_KEY_ID.in(correctWordKeyIds));
        correctWordMapper.deleteByQuery(queryWrapper);
        correctWordMapper.insertBatch(correctWords);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", correctWordKeys.size());
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
    public void downloadCorrectWordDataTemp(HttpServletResponse response) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            Resource resource = resourceLoader.getResource("classpath:temp/correctWordDataTemp.xlsx");
            inputStream = resource.getInputStream();

            // 设置响应头
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            // 获取文件名，并进行UTF-8编码
            String fileName = URLEncoder.encode("纠错词导入模板.xlsx", StandardCharsets.UTF_8.toString());
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
