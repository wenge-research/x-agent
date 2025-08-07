package com.wenge.model.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.dto.param.HotSearchWordParam;
import com.wenge.model.entity.HotSearchWord;
import com.wenge.model.mapper.HotSearchWordMapper;
import com.wenge.model.service.HotSearchWordService;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import net.sourceforge.pinyin4j.PinyinHelper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.HotSearchWordTableDef.HOT_SEARCH_WORD;

@Service
public class HotSearchWordServiceImpl extends ServiceImpl<HotSearchWordMapper, HotSearchWord> implements HotSearchWordService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private HotSearchWordService hotSearchWordService;

    private static final Pattern TONE_PATTERN = Pattern.compile("[0-5]");


    /**
     * 根据字符传分词返回分词后的列表
     *
     * @param context
     * @return
     */
    public List<String> addHotSearchWord(String context, String applicationId) {
        QueryWrapper queryWrapper = Wrappers.create()
                .where(HOT_SEARCH_WORD.APPLICATION_ID.eq(applicationId));
        List<HotSearchWord> queryAll = hotSearchWordService.list(queryWrapper);

        Map<String, HotSearchWord> hotSearchWordMap = queryAll.stream()
                .collect(Collectors.toMap(
                        HotSearchWord::getKeyword,
                        correctWordKey -> correctWordKey,
                        (existing, replacement) -> existing
                ));
        // 自动路由到引入的库去实现分词
        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<String> result = segmenter.sentenceProcess(context);
        List<?> stopWords = getStopWord();
        List<String> tokenizerWords = new ArrayList<>();
        for (String hotWord : result) {
            if (!stopWords.contains(hotWord)) {
                tokenizerWords.add(hotWord);
                HotSearchWord hotSearchWord = hotSearchWordMap.get(hotWord);
                if (ObjectUtils.isNotEmpty(hotSearchWord)) {
                    hotSearchWord.setCount(hotSearchWord.getCount() + 1);
                } else {
                    String pinyin = toPinyin(hotWord);
                    String firstPinyin = toFirstPinyin(hotWord);
                    hotSearchWord = new HotSearchWord();
                    hotSearchWord.setCount(1);
                    hotSearchWord.setApplicationId(applicationId);
                    hotSearchWord.setKeyword(hotWord);
                    hotSearchWord.setWordPy(pinyin);
                    hotSearchWord.setWordFirstPy(firstPinyin);
                }
                hotSearchWordMap.put(hotWord, hotSearchWord);
                saveOrUpdate(hotSearchWord);
            }
        }
        return tokenizerWords;
    }

    /**
     * 获取停用词
     *
     * @return
     */
    private List getStopWord() {
        List lines = null;
        // 先从缓存中获取
        String key = RedisKey.STOP_TOKEN;
        if (redisService.hasKey(key)) {
            lines = redisService.get(key, List.class);
        } else {
            try {
                // 加载暂停词
                lines = loadStopWords();
                redisService.set(RedisKey.STOP_TOKEN, lines);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lines;
    }

    /**
     * 加载暂停词
     *
     * @return
     * @throws IOException
     */
    private List<String> loadStopWords() throws IOException {
        // 使用类加载器获取资源输入流
        Resource resource = resourceLoader.getResource("classpath:stopWord/stopWord.txt");
        InputStream inputStream = resource.getInputStream();
        // 使用BufferedReader读取文件内容
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }


    /**
     * 查询热门搜索词
     *
     * @param hotSearchWordParam
     * @return
     */
    @Override
    public List<HotSearchWord> queryHotSearchWordTopN(HotSearchWordParam hotSearchWordParam) {
        QueryWrapper queryWrapper = Wrappers.init()
                .and(HOT_SEARCH_WORD.APPLICATION_ID
                        .eq(hotSearchWordParam.getApplicationId()))
                .and(StringUtils.isNotEmpty(hotSearchWordParam.getQuestion()),
                        HOT_SEARCH_WORD.KEYWORD.like(hotSearchWordParam.getQuestion())
                                .or(HOT_SEARCH_WORD.WORD_FIRST_PY.like(hotSearchWordParam.getQuestion()))
                                .or(HOT_SEARCH_WORD.WORD_PY.like(hotSearchWordParam.getQuestion()))
                )
                .orderBy(HOT_SEARCH_WORD.COUNT.desc())
                .limit(hotSearchWordParam.getQueryCount());

        return hotSearchWordService.list(queryWrapper);
    }

    /**
     * 汉字转拼音
     *
     * @param chinese 中文
     * @return 拼音
     */
    public static String toPinyin(String chinese) {
        StringBuilder outPy = new StringBuilder();

        for (char ch : chinese.toCharArray()) {
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(ch);
            if (pinyinArray != null) {
                String pinyin = pinyinArray[0];
                // 移除声调
                pinyin = TONE_PATTERN.matcher(pinyin).replaceAll("");
                outPy.append(pinyin);
            } else {
                outPy.append(ch);
            }
        }
        return outPy.toString().replace(" ", "");
    }

    /**
     * 汉字转拼音
     *
     * @param chinese 中文
     * @return 拼音
     */
    public static String toFirstPinyin(String chinese) {
        StringBuilder outPy = new StringBuilder();

        for (char ch : chinese.toCharArray()) {
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(ch);
            if (pinyinArray != null) {
                String pinyin = pinyinArray[0];
                // 移除声调
                pinyin = TONE_PATTERN.matcher(pinyin).replaceAll("");
                outPy.append(pinyin.charAt(0));
            } else {
                outPy.append(ch);
            }
        }
        return outPy.toString().replace(" ", "");
    }

}
