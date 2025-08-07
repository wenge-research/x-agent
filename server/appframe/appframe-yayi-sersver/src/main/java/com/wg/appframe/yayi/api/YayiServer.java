package com.wg.appframe.yayi.api;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.wg.appframe.yayi.config.SecurityConfig;
import com.wg.appframe.yayi.constants.StringConstans;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.*;
import com.wg.appframe.yayi.result.*;
import com.wg.appframe.yayi.strategy.YayiApiStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


@Slf4j
public class YayiServer {

    @Autowired(required = false)
    private Map<String, YayiApiStrategy> yayiApiStrategyMap;

    @Autowired(required = false)
    private SecurityConfig securityConfig;


    /**
     * 调用雅意【YAYI-13B】接口
     *
     * @param content
     * @return
     */
    public Generate13BResult generate13B(String content) {
        Generate13BParam param = new Generate13BParam();
        param.setStream(false);
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.YAYI_13B);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.generate13b >>>>>>>>>>>>");
            return new Generate13BResult();
        }
        return (Generate13BResult) yayiStrategy.run(content, param, null);
    }

    /**
     * 调用雅意【YAYI-13B】接口，返回文本回答
     *
     * @param content
     * @return
     */
    public String generate13BStr(String content) {
        Generate13BResult generate13BResult = generate13B(content);
        try {
            return generate13BResult.getData().getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            log.info("===> generate13BString.error:{}", e.getMessage());
        }
        return StringConstans.BLANK;
    }

    /**
     * 调用雅意【YAYI-13B】接口
     *
     * @param content
     * @return
     */
    public Generate13BResult generate13B(String content, Generate13BParam param) {
        if (null == param) {
            param = new Generate13BParam();
        }
        param.setStream(false);
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.YAYI_13B);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.generate13b >>>>>>>>>>>>");
            return new Generate13BResult();
        }
        return (Generate13BResult) yayiStrategy.run(content, param, null);
    }

    /**
     * 调用雅意【YAYI-13B】接口，返回文本回答
     *
     * @param content
     * @return
     */
    public String generate13BString(String content, Generate13BParam param) {
        Generate13BResult generate13BResult = generate13B(content, param);
        try {
            return generate13BResult.getData().getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            log.info("===> generate13BString.error:{}", e.getMessage());
        }
        return StringConstans.BLANK;
    }

    /**
     * 调用雅意【YAYI-13B】接口
     *
     * @param content
     * @return
     */
    public void generate13BStream(String content, Consumer<Generate13BResult> consumer) {
        Generate13BParam param = new Generate13BParam();
        param.setStream(true);
        param.setAccept("text/event-stream");
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.YAYI_13B);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.generate13b >>>>>>>>>>>>");
            return;
        }
        yayiStrategy.run(content, param, yayiResult -> consumer.accept((Generate13BResult) yayiResult));
    }

    /**
     * 调用雅意【YAYI-13B】接口，返回文本
     *
     * @param content
     * @return
     */
    public void generate13BStreamStr(String content, Consumer<String> consumer) {
        generate13BStream(content, p -> {
            try {
                String result = p.getData().getChoices().get(0).getMessage().getContent();
                consumer.accept(result);
            } catch (Exception e) {
                log.error("===> generate13BStreamStr.error:{}", e.getMessage());
                consumer.accept(StringConstans.BLANK);
            }
        });
    }

    /**
     * 调用雅意【YAYI-13B】接口
     *
     * @param content
     * @return
     */
    public void generate13BStream(String content, Generate13BParam param, Consumer<Generate13BResult> consumer) {
        if (null == param) {
            param = new Generate13BParam();
        }
        param.setStream(true);
        param.setAccept("text/event-stream");
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.YAYI_13B);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.generate13b >>>>>>>>>>>>");
            return;
        }
        yayiStrategy.run(content, param, yayiResult -> consumer.accept((Generate13BResult) yayiResult));
    }

    /**
     * 调用雅意【YAYI-13B】接口，返回文本
     *
     * @param content
     * @return
     */
    public void generate13BStreamStr(String content, Generate13BParam param, Consumer<String> consumer) {
        generate13BStream(content, param,p -> {
            try {
                String result = p.getData().getChoices().get(0).getMessage().getContent();
                consumer.accept(result);
            } catch (Exception e) {
                log.error("===> generate13BStreamStr.error:{}", e.getMessage());
                consumer.accept(StringConstans.BLANK);
            }
        });

    }

    /**
     * 调用雅意【YAYI-30B】接口
     *
     * @param content
     * @return
     */
    public Generate30BResult generate30B(String content) {
        Generate30BParam param = new Generate30BParam();
        param.setStream(false);
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.YAYI_30B);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.generate30b >>>>>>>>>>>>");
            return new Generate30BResult();
        }
        return (Generate30BResult) yayiStrategy.run(content, param, null);
    }

    /**
     * 调用雅意【YAYI-30B】接口
     *
     * @param content
     * @return
     */
    public String generate30BStr(String content) {
        Generate30BResult generate30BResult = generate30B(content);
        try {
            return generate30BResult.getData().getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            log.info("===> generate30BStr.error:{}", e.getMessage());
        }
        return StringConstans.BLANK;
    }

    /**
     * 调用雅意【YAYI-30B】接口
     *
     * @param content
     * @return
     */
    public Generate30BResult generate30B(String content, Generate30BParam param) {
        if (null == param) {
            param = new Generate30BParam();
        }
        param.setStream(false);
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.YAYI_30B);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.generate30b >>>>>>>>>>>>");
            return new Generate30BResult();
        }
        return (Generate30BResult) yayiStrategy.run(content, param, null);
    }

    /**
     * 调用雅意【YAYI-30B】接口
     *
     * @param content
     * @return
     */
    public String generate30BStr(String content, Generate30BParam param) {
        Generate30BResult generate30BResult = generate30B(content, param);
        try {
            return generate30BResult.getData().getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            log.info("===> generate30BStr.error:{}", e.getMessage());
        }
        return StringConstans.BLANK;
    }

    /**
     * 调用雅意【YAYI-30B】接口
     *
     * @param content
     * @return
     */
    public void generate30BStream(String content, Consumer<Generate30BResult> consumer) {
        Generate30BParam param = new Generate30BParam();
        param.setStream(true);
        param.setAccept("text/event-stream");
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.YAYI_30B);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.generate30b >>>>>>>>>>>>");
            return;
        }
        yayiStrategy.run(content, param, yayiResult -> consumer.accept((Generate30BResult) yayiResult));
    }

    /**
     * 调用雅意【YAYI-30B】接口
     *
     * @param content
     * @return
     */
    public void generate30BStreamStr(String content, Consumer<String> consumer) {
        generate30BStream(content, p -> {
            try {
                String result = p.getData().getChoices().get(0).getMessage().getContent();
                consumer.accept(result);
            } catch (Exception e) {
                log.error("===> generate30BStreamStr.error:{}", e.getMessage());
                consumer.accept(StringConstans.BLANK);
            }
        });
    }

    /**
     * 调用雅意【YAYI-30B】接口
     *
     * @param content
     * @return
     */
    public void generate30BStream(String content, Generate30BParam param, Consumer<Generate30BResult> consumer) {
        if (null == param) {
            param = new Generate30BParam();
        }
        param.setStream(true);
        param.setAccept("text/event-stream");
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.YAYI_30B);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.generate30b >>>>>>>>>>>>");
            return;
        }
        yayiStrategy.run(content, param, yayiResult -> consumer.accept((Generate30BResult) yayiResult));
    }

    /**
     * 调用雅意【YAYI-30B】接口
     *
     * @param content
     * @return
     */
    public void generate30BStreamStr(String content, Generate30BParam param, Consumer<String> consumer) {
        generate30BStream(content, param, p -> {
            try {
                String result = p.getData().getChoices().get(0).getMessage().getContent();
                consumer.accept(result);
            } catch (Exception e) {
                log.error("===> generate30BStreamStr.error:{}", e.getMessage());
                consumer.accept(StringConstans.BLANK);
            }
        });
    }

    /**
     * 调用雅意【YAYI-搜索】接口
     *
     * @param userMessage
     * @return
     */
    public PromptWebResult promptWeb(String userMessage, PromptWebParam param) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.YAYI_WEB_PROMPT);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.promptWeb >>>>>>>>>>>>");
            return new PromptWebResult();
        }
        if (null == param) {
            param = new PromptWebParam();
        }
        return (PromptWebResult) yayiStrategy.run(userMessage, param);
    }

    /**
     * 调用雅意【YAYI-搜索】接口
     *
     * @param userMessage
     * @return
     */
    public PromptWebResult promptWeb(String userMessage) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.YAYI_WEB_PROMPT);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.promptWeb >>>>>>>>>>>>");
            return new PromptWebResult();
        }
        PromptWebParam param = new PromptWebParam();
        return (PromptWebResult) yayiStrategy.run(userMessage, param);
    }

    /**
     * 调用雅意【YAYI-Embedding】接口
     *
     * @param contentList
     * @return
     */
    public EmbeddingResult embedding(List<String> contentList) {
        if (CollectionUtil.isEmpty(contentList)) {
            return new EmbeddingResult();
        }
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.YAYI_EMBEDDING);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.embedding >>>>>>>>>>>>");
            return new EmbeddingResult();
        }
        return (EmbeddingResult) yayiStrategy.run(contentList, new EmbeddingParam());
    }

    /**
     * 调用雅意【YAYI-Embedding】接口
     *
     * @param content
     * @return
     */
    public EmbeddingResult embedding(String content) {
        if (StringUtils.isBlank(content)) {
            return new EmbeddingResult();
        }
        List<String> contentList = ListUtil.toList(content);
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.YAYI_EMBEDDING);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.embedding >>>>>>>>>>>>");
            return new EmbeddingResult();
        }
        return (EmbeddingResult) yayiStrategy.run(contentList, new EmbeddingParam());
    }

    /**
     * 调用雅意【YAYI-Embedding】接口
     *
     * @param contentList
     * @return
     */
    public EmbeddingResult embedding(List<String> contentList, EmbeddingParam param) {
        if (CollectionUtil.isEmpty(contentList)) {
            return new EmbeddingResult();
        }
        if (null == param) {
            param = new EmbeddingParam();
        }
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.YAYI_EMBEDDING);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.embedding >>>>>>>>>>>>");
            return new EmbeddingResult();
        }
        return (EmbeddingResult) yayiStrategy.run(contentList, param);
    }

    /**
     * 调用雅意【YAYI-Embedding】接口
     *
     * @param content
     * @return
     */
    public EmbeddingResult embedding(String content, EmbeddingParam param) {
        if (StringUtils.isBlank(content)) {
            return new EmbeddingResult();
        }
        List<String> contentList = ListUtil.toList(content);
        if (null == param) {
            param = new EmbeddingParam();
        }
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.YAYI_EMBEDDING);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.embedding >>>>>>>>>>>>");
            return new EmbeddingResult();
        }
        return (EmbeddingResult) yayiStrategy.run(contentList, param);
    }

    /**
     * 调用雅意【YAYI2-128K】接口
     *
     * @param content
     * @return
     */
    public Generate128KResult generate128K(String content) {
        Generate128KParam param = new Generate128KParam();
        param.setStream(false);
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.YAYI_128K);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.generate128K >>>>>>>>>>>>");
            return new Generate128KResult();
        }
        return (Generate128KResult) yayiStrategy.run(content, param, null);
    }

    /**
     * 调用雅意【YAYI2-128K】接口
     *
     * @param content
     * @return
     */
    public String generate128KStr(String content) {
        Generate128KResult generate128KResult = generate128K(content);
        try {
            return generate128KResult.getData().getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            log.info("===> generate128KStr.error:{}", e.getMessage());
        }
        return StringConstans.BLANK;
    }

    /**
     * 调用雅意【YAYI2-128K】接口
     *
     * @param content
     * @return
     */
    public Generate128KResult generate128K(String content, Generate128KParam param) {
        if (null == param) {
            param = new Generate128KParam();
        }
        param.setStream(false);
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.YAYI_128K);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.generate128K >>>>>>>>>>>>");
            return new Generate128KResult();
        }
        return (Generate128KResult) yayiStrategy.run(content, param, null);
    }

    /**
     * 调用雅意【YAYI2-128K】接口
     *
     * @param content
     * @return
     */
    public String generate128KStr(String content, Generate128KParam param) {
        Generate128KResult generate128KResult = generate128K(content, param);
        try {
            return generate128KResult.getData().getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            log.info("===> generate128KStr.error:{}", e.getMessage());
        }
        return StringConstans.BLANK;
    }

    /**
     * 调用雅意【YAYI2-128K】接口
     *
     * @param content
     * @return
     */
    public void generate128KStream(String content, Consumer<Generate128KResult> consumer) {
        Generate128KParam param = new Generate128KParam();
        param.setStream(true);
        param.setAccept("text/event-stream");
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.YAYI_128K);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.generate128KStream >>>>>>>>>>>>");
            return;
        }
        yayiStrategy.run(content, param, yayiResult -> consumer.accept((Generate128KResult) yayiResult));
    }

    /**
     * 调用雅意【YAYI2-128K】接口
     *
     * @param content
     * @return
     */
    public void generate128KStreamStr(String content, Consumer<String> consumer) {
        generate128KStream(content, p -> {
            try {
                String result = p.getData().getChoices().get(0).getMessage().getContent();
                consumer.accept(result);
            } catch (Exception e) {
                log.error("===> generate128KStreamStr.error:{}", e.getMessage());
                consumer.accept(StringConstans.BLANK);
            }
        });
    }

    /**
     * 调用雅意【YAYI2-128K】接口
     *
     * @param content
     * @return
     */
    public void generate128KStream(String content, Generate128KParam param, Consumer<Generate128KResult> consumer) {
        if (null == param) {
            param = new Generate128KParam();
        }
        param.setStream(true);
        param.setAccept("text/event-stream");
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.YAYI_128K);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.generate128KStream >>>>>>>>>>>>");
            return;
        }
        yayiStrategy.run(content, param, yayiResult -> consumer.accept((Generate128KResult) yayiResult));
    }

    /**
     * 调用雅意【YAYI2-128K】接口
     *
     * @param content
     * @return
     */
    public void generate128KStreamStr(String content, Generate128KParam param, Consumer<String> consumer) {
        generate128KStream(content, param, p -> {
            try {
                String result = p.getData().getChoices().get(0).getMessage().getContent();
                consumer.accept(result);
            } catch (Exception e) {
                log.error("===> generate128KStreamStr.error:{}", e.getMessage());
                consumer.accept(StringConstans.BLANK);
            }
        });
    }

    /**
     * 调用雅意【YaYi-重排模型】接口
     *
     * @param param
     * @return
     */
    public RearrangeResult rearrange(RearrangeParam param) {
        if (null == param || null == param.getContent() || CollectionUtil.isEmpty(param.getContent().getArticles()) || StringUtils.isBlank(param.getContent().getQuery())) {
            return new RearrangeResult();
        }
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.REARRANGE);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.rearrange >>>>>>>>>>>>");
            return new RearrangeResult();
        }
        return (RearrangeResult) yayiStrategy.run(param);
    }

    /**
     * 调用雅意【YAYI-对话记录筛选】接口
     *
     * @param param
     * @return
     */
    public DialogueFilterResult dialogueFilter(List<YayiMessage> contents, DialogueFilterParam param) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.DIALOGUE_FILTER);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.dialogue >>>>>>>>>>>>");
            return new DialogueFilterResult();
        }
        if (null == param) {
            param = new DialogueFilterParam();
        }
        return (DialogueFilterResult) yayiStrategy.runMsg(contents, param);
    }

    /**
     * 调用雅意【YAYI-对话记录筛选】接口
     *
     * @param contents
     * @return
     */
    public DialogueFilterResult dialogueFilter(List<YayiMessage> contents) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.DIALOGUE_FILTER);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.dialogue >>>>>>>>>>>>");
            return new DialogueFilterResult();
        }
        DialogueFilterParam param = new DialogueFilterParam();
        return (DialogueFilterResult) yayiStrategy.runMsg(contents, param);
    }

    /**
     * 调用雅意【文档智能解析】接口
     *
     * @param fileUrl
     * @return
     */
    public DocumentResult documentAnalysis(String fileUrl) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.DOCUMENT);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.document >>>>>>>>>>>>");
            return new DocumentResult();
        }
        DocumentParam param = new DocumentParam();
        return (DocumentResult) yayiStrategy.run(fileUrl, param);
    }

    /**
     * 调用雅意【文档智能解析】接口
     *
     * @param fileUrl
     * @return
     */
    public DocumentResult documentAnalysis(String fileUrl, DocumentParam param) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.DOCUMENT);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.document >>>>>>>>>>>>");
            return new DocumentResult();
        }
        if (null == param) {
            param = new DocumentParam();
        }
        return (DocumentResult) yayiStrategy.run(fileUrl, param);
    }

     /**
     * 调用雅意【文档智能解析】接口
     *
     * @param fileUrl
     * @return
     */
    public List<DocumentResult.FileContent> documentAnalysisList(String fileUrl, DocumentParam param) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.DOCUMENT);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.document >>>>>>>>>>>>");
            return ListUtil.toList();
        }
        if (null == param) {
            param = new DocumentParam();
        }
        DocumentResult result = (DocumentResult) yayiStrategy.run(fileUrl, param);
        if (StringConstans.CODE.equals(result.getCode())) {
            DocumentResult.DocumentData data = result.getData();
            if (null != data) {
                return data.getFile_content();
            }
        }
        return ListUtil.toList();
    }

    /**
     * 调用雅意【文档智能解析】接口
     *
     * @param fileUrl
     * @return
     */
    public List<DocumentResult.FileContent> documentAnalysisList(String fileUrl) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.DOCUMENT);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.document >>>>>>>>>>>>");
            return ListUtil.toList();
        }
        DocumentParam param = new DocumentParam();
        DocumentResult result = (DocumentResult) yayiStrategy.run(fileUrl, param);
        if (StringConstans.CODE.equals(result.getCode())) {
            DocumentResult.DocumentData data = result.getData();
            if (null != data) {
                return data.getFile_content();
            }
        }
        return ListUtil.toList();
    }

    /**
     * 调用雅意【YAYI-Security】接口
     *
     * @param content
     * @return
     */
    public SecurityResult security(String content, SecurityParam param) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.SECURITY);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.security >>>>>>>>>>>>");
            return new SecurityResult();
        }
        if (null == param) {
            param = new SecurityParam();
        }
        return (SecurityResult) yayiStrategy.run(content, param);
    }

    /**
     * 调用雅意【YAYI-Security】接口
     *
     * @param content
     * @return
     */
    public SecurityResult security(String content) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.SECURITY);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.security >>>>>>>>>>>>");
            return new SecurityResult();
        }
        SecurityParam   param = new SecurityParam();
        return (SecurityResult) yayiStrategy.run(content, param);
    }

    /**
     * 调用雅意【YAYI-Security】接口
     *
     * @param content
     * @return
     */
    public Boolean securityBoolean(String content, SecurityParam param) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.SECURITY);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.security >>>>>>>>>>>>");
            return false;
        }
        if (null == param) {
            param = new SecurityParam();
        }
        SecurityResult result = (SecurityResult) yayiStrategy.run(content, param);
        if (StringConstans.CODE.equals(result.getCode())) {
            SecurityResult.Data data = result.getData();
            if (null != data) {
                if (data.getIs_danger() == 1) {
                    // 指定的敏感等级不算敏感词
                    if (CollectionUtil.isNotEmpty(securityConfig.getSecurityLevel())) {
                        for (String level : securityConfig.getSecurityLevel()) {
                            if (StringUtils.isNotBlank(data.getDanger_words()) && data.getDanger_words().contains(level)) {
                                return false;
                            }
                        }
                    }
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    /**
     * 调用雅意【YAYI-Security】接口
     *
     * @param content
     * @return
     */
    public Boolean securityBoolean(String content) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.SECURITY);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.security >>>>>>>>>>>>");
            return false;
        }
        SecurityParam param = new SecurityParam();
        SecurityResult result = (SecurityResult) yayiStrategy.run(content, param);
        if (StringConstans.CODE.equals(result.getCode())) {
            SecurityResult.Data data = result.getData();
            if (null != data) {
                if (data.getIs_danger() == 1) {
                    // 指定的敏感等级不算敏感词
                    if (CollectionUtil.isNotEmpty(securityConfig.getSecurityLevel())) {
                        for (String level : securityConfig.getSecurityLevel()) {
                            if (StringUtils.isNotBlank(data.getDanger_words()) && data.getDanger_words().contains(level)) {
                                return false;
                            }
                        }
                    }
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    /**
     * 调用雅意【YAYI-推荐问题生成】接口
     *
     * @param content
     * @return
     */
    public QuestionResult question(String content, QuestionParam param) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.QUESTION);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.question >>>>>>>>>>>>");
            return new QuestionResult();
        }
        if (null == param) {
            param = new QuestionParam();
        }
        return (QuestionResult) yayiStrategy.run(content, param);
    }

    /**
     * 调用雅意【YAYI-推荐问题生成】接口
     *
     * @param content
     * @return
     */
    public QuestionResult question(String content) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.QUESTION);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.question >>>>>>>>>>>>");
            return new QuestionResult();
        }
        QuestionParam param = new QuestionParam();
        return (QuestionResult) yayiStrategy.run(content, param);
    }

    /**
     * 调用雅意【URL转文本】接口
     *
     * @param url
     * @return
     */
    public UrlTextResult url2Text(String url, UrlTextParam param) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.URL_TEXT);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.urltext >>>>>>>>>>>>");
            return new UrlTextResult();
        }
        if (null == param) {
            param = new UrlTextParam();
        }
        return (UrlTextResult) yayiStrategy.run(url, param);
    }

    /**
     * 调用雅意【URL转文本】接口
     *
     * @param url
     * @return
     */
    public UrlTextResult url2Text(String url) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.URL_TEXT);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.urltext >>>>>>>>>>>>");
            return new UrlTextResult();
        }
        UrlTextParam param = new UrlTextParam();
        return (UrlTextResult) yayiStrategy.run(url, param);
    }

    /**
     * 调用雅意【YAYI-知识库】接口
     *
     * @param param
     * @return
     */
    public KnowledgeResult knowledge(KnowledgeParam param) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.KNOWLEDGE);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.knowledge >>>>>>>>>>>>");
            return new KnowledgeResult();
        }
        if (null == param) {
            return new KnowledgeResult();
        }
        if (StringUtils.isBlank(param.getModel())) {
            log.error("知识库的服务model请选择{}", KnowledgeParam.KNOWLEDGE_CONFIG);
            return new KnowledgeResult();
        }
        return (KnowledgeResult) yayiStrategy.run(param);
    }

    /**
     * 调用雅意【YAYI-知识库】接口
     *
     * @param param
     * @return
     */
    public KnowledgeResult knowledgeSplit(KnowledgeParam param) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.KNOWLEDGE_SPLIT);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.knowledgeSplit >>>>>>>>>>>>");
            return new KnowledgeResult();
        }
        if (null == param) {
            return new KnowledgeResult();
        }
        if (StringUtils.isBlank(param.getModel())) {
            log.error("知识库的服务model请选择{}", KnowledgeParam.KNOWLEDGE_SPLIT_CONFIG);
            return new KnowledgeResult();
        }
        return (KnowledgeResult) yayiStrategy.run(param);
    }

    /**
     * 调用雅意【YAYI-内容解析】接口
     *
     * @param fileUrl
     * @param param
     * @return
     */
    public ContentParsingResult contentParsing(String fileUrl, ContentParsingParam param) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.CONTENT_PARSING);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.contentparsing >>>>>>>>>>>>");
            return new ContentParsingResult();
        }
        if (null == param) {
            return new ContentParsingResult();
        }
        return (ContentParsingResult) yayiStrategy.run(fileUrl, param);
    }

    /**
     * 调用雅意【YAYI-内容解析】接口
     *
     * @param fileUrl
     * @return
     */
    public ContentParsingResult contentParsing(String fileUrl) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.CONTENT_PARSING);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.contentparsing >>>>>>>>>>>>");
            return new ContentParsingResult();
        }
        ContentParsingParam param = new ContentParsingParam();
        return (ContentParsingResult) yayiStrategy.run(fileUrl, param);
    }

    /**
     * 调用雅意【YAYI-Visual-17B】接口
     *
     * @param fileUrl
     * @return
     */
    public VisualResult visual(String fileUrl, VisualParam param) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.VISUAL_STRATEGY);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.visual >>>>>>>>>>>>");
            return new VisualResult();
        }
        if (null == param) {
            param = new VisualParam();
        }
        return (VisualResult) yayiStrategy.run(fileUrl, param);
    }

    /**
     * 调用雅意【YAYI-Visual-17B】接口
     *
     * @param fileUrl
     * @return
     */
    public VisualResult visual(String fileUrl, String question) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.VISUAL_STRATEGY);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.visual >>>>>>>>>>>>");
            return new VisualResult();
        }
        VisualParam param = new VisualParam();
        List<YayiMessage> messages = ListUtil.toList();
        param.setMessages(messages);
        messages.add(new YayiMessage("user", question));
        return (VisualResult) yayiStrategy.run(fileUrl, param);
    }

    /**
     * 调用雅意【多语种机器翻译多语种机器翻译】接口，中文翻译成英文
     *
     * @param text
     * @return
     */
    public YayiTranslationResult translateZh2En(String text) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.YAYI_TRANSLATION);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.translation >>>>>>>>>>>>");
            return new YayiTranslationResult();
        }
        YayiTranslationParam param = new YayiTranslationParam();
        YayiTranslationParam.Content content = new YayiTranslationParam.Content();
        param.setContent(content);
        content.setSrc_lang("zh");
        content.setTgt_lang("en");
        return (YayiTranslationResult) yayiStrategy.run(text, param);
    }

    /**
     * 调用雅意【多语种机器翻译多语种机器翻译】接口
     *
     * @param text
     * @return
     */
    public YayiTranslationResult yayiTranslate(String text, String srcLang, String tgtLang) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.YAYI_TRANSLATION);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.translation >>>>>>>>>>>>");
            return new YayiTranslationResult();
        }
        YayiTranslationParam param = new YayiTranslationParam();
        YayiTranslationParam.Content content = new YayiTranslationParam.Content();
        param.setContent(content);
        content.setSrc_lang(srcLang);
        content.setTgt_lang(tgtLang);
        return (YayiTranslationResult) yayiStrategy.run(text, param);
    }


    /**
     * AI作画
     *
     * @param param
     * @return
     */
    public AiImageResult aiImage(AiImageParam param) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.AI_IMAGE);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.aiimage >>>>>>>>>>>>");
            return new AiImageResult();
        }
        return (AiImageResult) yayiStrategy.run("", param);

    }


    /**
     * 雅意内容解析新版本
     *
     * @param fileUrl
     * @param param
     * @return
     */
    public ContentParsingNewVersionResult contentParsingNewVersion(String fileUrl, ContentParsingParam param) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.CONTENT_PARSING_NEW_VERSION);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.contentparsingNewVersion >>>>>>>>>>>>");
            return new ContentParsingNewVersionResult();
        }
        if (null == param) {
            return new ContentParsingNewVersionResult();
        }
        return (ContentParsingNewVersionResult) yayiStrategy.run(fileUrl, param);
    }

    /**
     * 雅意内容解析新版本
     *
     * @param fileUrl
     * @return
     */
    public ContentParsingNewVersionResult contentParsingNewVersion(String fileUrl) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.CONTENT_PARSING_NEW_VERSION);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.contentparsingNewVersion >>>>>>>>>>>>");
            return new ContentParsingNewVersionResult();
        }
        ContentParsingNewVersionParam param = new ContentParsingNewVersionParam();
        return (ContentParsingNewVersionResult) yayiStrategy.run(fileUrl, param);
    }

    /**
     * 雅意图片识别
     *
     * @param imageUrl
     * @return
     */
    public ImageIdentifyResult imageIdentify(String imageUrl) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.IMAGE_IDENTIFY);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.imageIdentify >>>>>>>>>>>>");
            return new ImageIdentifyResult();
        }
        ImageIdentifyParam param = new ImageIdentifyParam();
        return (ImageIdentifyResult) yayiStrategy.run(imageUrl, param);
    }


    /**
     * 雅意插件选择模型
     *
     * @param param
     * @return
     */
    public YayiPluginSelectionModelResult pluginModel(YayiPluginSelectionModelParam param) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.PLUGIN_SELECTION_MODEL);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.plugin >>>>>>>>>>>>");
            return new YayiPluginSelectionModelResult();
        }
        return (YayiPluginSelectionModelResult) yayiStrategy.run(param);
    }

    /**
     * 雅意添加文档索引
     *
     * @param param
     * @return
     */
    public YayiContentIndexResult indexContent(YayiContentIndexParam param) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.CONTENT_INDEX);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.index >>>>>>>>>>>>");
            return new YayiContentIndexResult();
        }
        return (YayiContentIndexResult) yayiStrategy.run(param);
    }

    /**
     * 调用雅意【YAYI-CHART】接口
     *
     * @param url
     * @return
     */
    public ChartResult chart(String url, ChartParam param) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.CHART_STRATEGY);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.chart >>>>>>>>>>>>");
            return new ChartResult();
        }
        if (null == param) {
            param = new ChartParam();
        }
        return (ChartResult) yayiStrategy.run(url, param);
    }

    /**
     * 调用雅意【YAYI-CHART】接口
     *
     * @param fileUrl
     * @return
     */
    public ChartResult chart(String fileUrl, String question) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.CHART_STRATEGY);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.chart >>>>>>>>>>>>");
            return new ChartResult();
        }
        ChartParam param = new ChartParam();
        List<YayiMessage> messages = ListUtil.toList();
        param.setMessages(messages);
        messages.add(new YayiMessage("user", question));
        return (ChartResult) yayiStrategy.run(fileUrl, param);
    }

    /**
     * 调用雅意【YAYI-数学模型】接口
     *
     * @param question
     * @return
     */
    public MathModelResult mathModel(String question) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.MATH_MODEL_STRATEGY);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.mathmodel >>>>>>>>>>>>");
            return new MathModelResult();
        }
        MathModelParam   param = new MathModelParam();
        return (MathModelResult) yayiStrategy.run(question, param);
    }

    /**
     * 调用雅意【YAYI-数学模型】接口
     *
     * @param question
     * @return
     */
    public MathModelResult mathModel(String question, MathModelParam param) {
        YayiApiStrategy yayiStrategy = yayiApiStrategyMap.get(StringConstans.MATH_MODEL_STRATEGY);
        if (null == yayiStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.yayi.mathmodel >>>>>>>>>>>>");
            return new MathModelResult();
        }
        if (null == param) {
            param = new MathModelParam();
        }
        return (MathModelResult) yayiStrategy.run(question, param);
    }

}

