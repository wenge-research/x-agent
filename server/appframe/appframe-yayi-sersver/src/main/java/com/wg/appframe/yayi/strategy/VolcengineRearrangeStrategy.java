package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.VolcengineConfig;
import com.wg.appframe.yayi.config.VolcengineRearrangeConfig;
import com.wg.appframe.yayi.param.VolcengineParam;
import com.wg.appframe.yayi.param.VolcengineRearrangeParam;
import com.wg.appframe.yayi.result.VolcengineRearrangeResult;
import com.wg.appframe.yayi.result.VolcengineResult;
import com.wg.appframe.yayi.utils.VolcengineApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class VolcengineRearrangeStrategy implements VolcengineApiStrategy{


    @Autowired(required = false)
    private VolcengineRearrangeConfig rearrangeConfig;

    @Autowired(required = false)
    private VolcengineConfig volcengineConfig;

    
    public VolcengineResult run(VolcengineParam volcengineParam) {
        VolcengineRearrangeResult result = new VolcengineRearrangeResult();
        if (volcengineParam instanceof VolcengineRearrangeParam) {
            VolcengineRearrangeParam param = (VolcengineRearrangeParam) volcengineParam;
            result = runRearrange(param);
        }

        return result;
    }

    /**
     * 调用重排模型接口
     * @param volcengineParam
     * @return
     */
    public VolcengineRearrangeResult runRearrange(VolcengineRearrangeParam volcengineParam) {
        VolcengineRearrangeResult result = new VolcengineRearrangeResult();
        try {
            // 封装重排模型接口入参
            VolcengineRearrangeParam rearrangeParam = rearrangeParam(volcengineParam);

            // 设置公共请求参数
            BeanUtil.copyProperties(this.volcengineConfig, rearrangeParam);

            // 请求重排模型接口
            result = getResult(rearrangeParam);
            return result;
        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }

        return result;
    }

    /**
     * 初始化重排模型接口参数
     * @param param
     * @return
     */
    private VolcengineRearrangeParam rearrangeParam(VolcengineRearrangeParam param) {
        VolcengineRearrangeParam rearrangeParam = new VolcengineRearrangeParam();
        List<VolcengineRearrangeParam.Article> articles = new ArrayList<>();
        rearrangeParam.setDatas(articles);
        if (null != rearrangeConfig.getRerankModel()) {
            rearrangeParam.setRerank_model(rearrangeConfig.getRerankModel());
        }
        if (StringUtils.isBlank(rearrangeParam.getUri())) {
            rearrangeParam.setUri(rearrangeConfig.getUri());
        }

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        List<VolcengineRearrangeParam.Article> articles1 = param.getDatas();
        if (CollectionUtil.isNotEmpty(articles1)) {
            BeanUtil.copyProperties(articles1, articles, copyOptions);
            BeanUtil.copyProperties(articles, articles1, copyOptions);
        }
        BeanUtil.copyProperties(param, rearrangeParam, copyOptions);
        BeanUtil.copyProperties(rearrangeParam, param, copyOptions);
        return rearrangeParam;
    }

    /**
     * 获取结果
     *
     * @param volcengineParam
     * @return
     */
    private VolcengineRearrangeResult getResult(VolcengineParam volcengineParam) throws Exception {
        VolcengineRearrangeResult result = new VolcengineRearrangeResult();
        log.info("===>runVolcengineRearrange重排模型 url:{}  ,  param: {}", volcengineParam.getUri(), JSONUtil.toJsonStr(volcengineParam));
        String body = VolcengineApiUtils.invokeVolcengineApi(volcengineParam);
        log.info("===>runVolcengineRearrange重排模型 body: {}", body);
        if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
            JSONObject entries = JSONUtil.parseObj(body);
            result = entries.toBean(VolcengineRearrangeResult.class);
        }
        if (null == result) {
            return new VolcengineRearrangeResult();
        }
        // 对原始结果进行排序处理
        sortRerrangeData(result);

        return result;
    }

    /**
     * 对原始重排结果进行倒序处理
     * @param result
     */
    private static void sortRerrangeData(VolcengineRearrangeResult result) {
        if (null == result || null == result.getData()) {
            return;
        }

        VolcengineRearrangeResult.VolcengineRearrangeData data = result.getData();
        List<BigDecimal> scores = data.getScores();
        if (CollectionUtil.isEmpty(scores)) {
            return;
        }
        // 使用 IntStream 将索引与值配对，按值降序排序后保留原始索引
        List<Integer> indices = IntStream.range(0, scores.size())
                .boxed()
                .sorted((i1, i2) -> scores.get(i2).compareTo(scores.get(i1)))
                .collect(Collectors.toList());

        // 提取排序后的值和索引（从0开始）
        List<BigDecimal> sortedValues = indices.stream()
                .map(scores::get)
                .collect(Collectors.toList());

        data.setIndex_list(indices);
        data.setRes_scores_list(sortedValues);
    }

}
