package com.wg.appframe.yayi.api;

import cn.hutool.core.collection.CollectionUtil;
import com.wg.appframe.yayi.constants.StringConstans;
import com.wg.appframe.yayi.param.DoubaoImageParam;
import com.wg.appframe.yayi.param.DoubaoVideoParam;
import com.wg.appframe.yayi.param.RearrangeParam;
import com.wg.appframe.yayi.param.VolcengineRearrangeParam;
import com.wg.appframe.yayi.result.DoubaoImageResult;
import com.wg.appframe.yayi.result.DoubaoVideoResult;
import com.wg.appframe.yayi.result.RearrangeResult;
import com.wg.appframe.yayi.result.VolcengineRearrangeResult;
import com.wg.appframe.yayi.strategy.DoubaoImageStrategy;
import com.wg.appframe.yayi.strategy.DoubaoVideoStrategy;
import com.wg.appframe.yayi.strategy.VolcengineApiStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
public class VolcengineServer {

    @Autowired(required = false)
    private Map<String, VolcengineApiStrategy> volcengineApiStrategyMap;

    @Autowired(required = false)
    private DoubaoImageStrategy doubaoImageStrategy;

    @Autowired(required = false)
    private DoubaoVideoStrategy doubaoVideoStrategy;

    /**
     * 调用火山引擎【重排】接口
     *
     * @param param
     * @return
     */
    public RearrangeResult rearrange(RearrangeParam param) {
        if (null == param || null == param.getContent() || CollectionUtil.isEmpty(param.getContent().getArticles())
                || StringUtils.isBlank(param.getContent().getQuery())) {
            return new RearrangeResult();
        }
        VolcengineApiStrategy volcengineApiStrategy = volcengineApiStrategyMap.get(StringConstans.VOLCENGINE_REARRANGE);
        if (null == volcengineApiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.volcengine.rearrange >>>>>>>>>>>>");
            return new RearrangeResult();
        }
        // 封装入参
        List<RearrangeParam.Articles> originArticles = param.getContent().getArticles();
        VolcengineRearrangeParam volcengineRearrangeParam = new VolcengineRearrangeParam();
        List<VolcengineRearrangeParam.Article> articles = new ArrayList<>();
        originArticles.forEach(v -> {
            VolcengineRearrangeParam.Article article = new VolcengineRearrangeParam.Article();
            article.setQuery(param.getContent().getQuery());
            article.setTitle(StringUtils.isBlank(v.getTitle())? StringConstans.BLANK : v.getTitle());
            article.setContent(StringUtils.isBlank(v.getPara())? StringConstans.BLANK : v.getPara());
            articles.add(article);
        });
        volcengineRearrangeParam.setDatas(articles);
        VolcengineRearrangeResult volcengineRearrangeResult = (VolcengineRearrangeResult) volcengineApiStrategy.run(volcengineRearrangeParam);
        if (null == volcengineRearrangeResult || null == volcengineRearrangeResult.getData()) {
            return new RearrangeResult();
        }

        // 设置uri
        param.setUri(volcengineRearrangeParam.getUri());

        // 封装返回结果
        RearrangeResult result = new RearrangeResult();
        RearrangeResult.RearrangeData rearrangeData = new RearrangeResult.RearrangeData();
        rearrangeData.setIndex_list(volcengineRearrangeResult.getData().getIndex_list());
        rearrangeData.setRes_scores_list(volcengineRearrangeResult.getData().getRes_scores_list());
        result.setData(rearrangeData);
        result.setRetryFlag(false);
        result.setCode(volcengineRearrangeResult.getCode());
        result.setMsg(volcengineRearrangeResult.getMessage());

        return result;
    }

    /**
     * 文生图
     * @param content
     * @return
     */
    public DoubaoImageResult aiImage(String content) {
        return doubaoImageStrategy.run(content, new DoubaoImageParam());
    }

    /**
     * 文生图
     * @param content
     * @param param
     * @return
     */
    public DoubaoImageResult aiImage(String content, DoubaoImageParam param) {
        return doubaoImageStrategy.run(content, param);
    }

    /**
     * 视频生成
     * @param content
     * @return
     */
    public DoubaoVideoResult aiVideo(String content) {
        return doubaoVideoStrategy.run(content, new DoubaoVideoParam());
    }

    /**
     * 视频生成
     * @param content
     * @param param
     * @return
     */
    public DoubaoVideoResult aiVideo(String content, DoubaoVideoParam param) {
        return doubaoVideoStrategy.run(content, param);
    }

    /**
     * 视频任务查询
     * @param id
     * @param apikey
     * @return
     */
    public DoubaoVideoResult tasks(String id, String apikey) {
        return doubaoVideoStrategy.tasks(id, apikey);
    }
}

