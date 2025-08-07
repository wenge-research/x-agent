package com.wg.appframe.yayi.config;

import cn.hutool.extra.spring.SpringUtil;
import com.wg.appframe.yayi.api.*;
import com.wg.appframe.yayi.constants.StringConstans;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.strategy.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@AutoConfiguration
@Slf4j
@ComponentScan("com.wg.appframe")
public class YayiAutoConfigure implements ApplicationContextInitializer<GenericApplicationContext> {

    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.generate13b")
    @ConditionalOnProperty(prefix = "appframe.yayi.generate13b", name = "uri")
    public Yayi13BConfig yayi13BConfig() {
        return new Yayi13BConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.config")
    @ConditionalOnProperty(prefix = "appframe.yayi.config", name = "host")
    public YayiConfig yayiConfig() {
        return new YayiConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.volcengine.config")
    @ConditionalOnProperty(prefix = "appframe.volcengine.config", name = "host")
    public VolcengineConfig volcengineConfig() {
        return new VolcengineConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.kimi.config")
    @ConditionalOnProperty(prefix = "appframe.kimi.config", name = "host")
    public KimiConfig kimiConfig() {
        return new KimiConfig();
    }
    @Bean
    @ConfigurationProperties(prefix = "appframe.siliconflow.config")
    @ConditionalOnProperty(prefix = "appframe.siliconflow.config", name = "host")
    public SiliconflowConfig siliconflowConfig() {
        return new SiliconflowConfig();
    }
    @Bean
    @ConfigurationProperties(prefix = "appframe.zhipu.config")
    @ConditionalOnProperty(prefix = "appframe.zhipu.config", name = "host")
    public ZhipuConfig zhipuConfig() {
        return new ZhipuConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.deepseek.config")
    @ConditionalOnProperty(prefix = "appframe.deepseek.config", name = "host")
    public DeepSeekConfig deepSeekConfig() {
        return new DeepSeekConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.wenxin.config")
    @ConditionalOnProperty(prefix = "appframe.wenxin.config", name = "host")
    public WenxinConfig wenxinConfig() {
        return new WenxinConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.doubao.config")
    @ConditionalOnProperty(prefix = "appframe.doubao.config", name = "host")
    public DoubaoConfig doubaoConfig() {
        return new DoubaoConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.minimax.config")
    @ConditionalOnProperty(prefix = "appframe.minimax.config", name = "host")
    public MinimaxConfig minimaxConfig() {
        return new MinimaxConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.promptweb")
    @ConditionalOnProperty(prefix = "appframe.yayi.promptweb", name = "uri")
    public WebPromptConfig webPromptConfig() {
        return new WebPromptConfig();
    }
    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.document")
    @ConditionalOnProperty(prefix = "appframe.yayi.document", name = "uri")
    public DocumentConfig documentConfig() {
        return new DocumentConfig();
    }
    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.visual")
    @ConditionalOnProperty(prefix = "appframe.yayi.visual", name = "uri")
    public VisualConfig visualConfig() {
        return new VisualConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.chart")
    @ConditionalOnProperty(prefix = "appframe.yayi.chart", name = "uri")
    public ChartConfig chartConfig() {
        return new ChartConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.mathmodel")
    @ConditionalOnProperty(prefix = "appframe.yayi.mathmodel", name = "uri")
    public MathModelConfig mathModelConfig() {
        return new MathModelConfig();
    }
    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.contentparsing")
    @ConditionalOnProperty(prefix = "appframe.yayi.contentparsing", name = "uri")
    public ContentParsingConfig contentParsingConfig() {
        return new ContentParsingConfig();
    }
    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.aiimage")
    @ConditionalOnProperty(prefix = "appframe.yayi.aiimage", name = "uri")
    public AiImageConfig aiImageConfig() {
        return new AiImageConfig();
    }
    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.translation")
    @ConditionalOnProperty(prefix = "appframe.yayi.translation", name = "uri")
    public YayiTranslationConfig yayiTranslationConfig() {
        return new YayiTranslationConfig();
    }
    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.knowledge")
    @ConditionalOnProperty(prefix = "appframe.yayi.knowledge", name = "uri")
    public KnowledgeConfig knowledgeConfig() {
        return new KnowledgeConfig();
    }
    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.knowledgesplit")
    @ConditionalOnProperty(prefix = "appframe.yayi.knowledgesplit", name = "uri")
    public KnowledgeSplitConfig knowledgeSplitConfig() {
        return new KnowledgeSplitConfig();
    }
    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.security")
    @ConditionalOnProperty(prefix = "appframe.yayi.security", name = "uri")
    public SecurityConfig securityConfig() {
        return new SecurityConfig();
    }
    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.contentparsingnewversion")
    @ConditionalOnProperty(prefix = "appframe.yayi.contentparsingnewversion", name = "uri")
    public ContentParsingNewVersionConfig contentParsingNewVersionConfig() {
        return new ContentParsingNewVersionConfig();
    }
    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.imageidentify")
    @ConditionalOnProperty(prefix = "appframe.yayi.imageidentify", name = "uri")
    public ImageIdentifyConfig imageIdentifyConfig() {
        return new ImageIdentifyConfig();
    }
    @Bean
    @ConfigurationProperties(prefix = "appframe.volcengine.rearrange")
    @ConditionalOnProperty(prefix = "appframe.volcengine.rearrange", name = "uri")
    public VolcengineRearrangeConfig volcengineRearrangeConfig() {
        return new VolcengineRearrangeConfig();
    }

    @Bean
    @ConditionalOnBean(value = {YayiConfig.class})
    public YayiServer yayiServer() {
        return new YayiServer();
    }

    @Bean
    @ConditionalOnBean(value = {VolcengineConfig.class})
    public VolcengineServer volcengineServer() {
        return new VolcengineServer();
    }

    @Bean
    @ConditionalOnBean(value = {KimiConfig.class})
    public KimiServer kimiServer() {
        return new KimiServer();
    }
   @Bean
    @ConditionalOnBean(value = {DeepSeekConfig.class})
    public DeepseekServer deepseekServer() {
        return new DeepseekServer();
    }
    @Bean
    @ConditionalOnBean(value = {SiliconflowConfig.class})
    public SiliconFlowServer siliconFlowServer() {
        return new SiliconFlowServer();
    }

   @Bean
    @ConditionalOnBean(value = {WenxinConfig.class})
    public WenxinServer wenxinServer() {
        return new WenxinServer();
    }
   @Bean
    @ConditionalOnBean(value = {ZhipuConfig.class})
    public ZhipuServer zhipuServer() {
        return new ZhipuServer();
    }

   @Bean
    @ConditionalOnBean(value = {DoubaoConfig.class})
    public DoubaoServer doubaoServer() {
        return new DoubaoServer();
    }


   @Bean
    @ConditionalOnBean(value = {MinimaxConfig.class})
    public MinimaxiServer minimaxiServer() {
        return new MinimaxiServer();
    }

    @Bean
    public GeneralServer generalServer() {
        return new GeneralServer();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.generate30b")
    @ConditionalOnProperty(prefix = "appframe.yayi.generate30b", name = "uri")
    public Yayi30BConfig yayi30BConfig() {
        return new Yayi30BConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.kimi.completions")
    @ConditionalOnProperty(prefix = "appframe.kimi.completions", name = "uri")
    public KimiCompletionsConfig kimiCompletionsConfig() {
        return new KimiCompletionsConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.minimax.completions")
    @ConditionalOnProperty(prefix = "appframe.minimax.completions", name = "uri")
    public MinmaxCompletionsConfig minmaxCompletionsConfig() {
        return new MinmaxCompletionsConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.minimax.image")
    @ConditionalOnProperty(prefix = "appframe.minimax.image", name = "uri")
    public MinmaxImageConfig minmaxImageConfig() {
        return new MinmaxImageConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.minimax.video")
    @ConditionalOnProperty(prefix = "appframe.minimax.video", name = "uri")
    public MinmaxVideoConfig minmaxVideoConfig() {
        return new MinmaxVideoConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.minimax.music")
    @ConditionalOnProperty(prefix = "appframe.minimax.music", name = "uri")
    public MinmaxMusicConfig minmaxMusicConfig() {
        return new MinmaxMusicConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.wenxin.chat")
    @ConditionalOnProperty(prefix = "appframe.wenxin.chat", name = "uri")
    public WenxinChatConfig wenxinChatConfig() {
        return new WenxinChatConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.deepseek.completions")
    @ConditionalOnProperty(prefix = "appframe.deepseek.completions", name = "uri")
    public DeepseekCompletionsConfig deepseekCompletionsConfig() {
        return new DeepseekCompletionsConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.siliconflow.completions")
    @ConditionalOnProperty(prefix = "appframe.siliconflow.completions", name = "uri")
    public SiliconFlowCompletionsConfig siliconFlowCompletionsConfig() {
        return new SiliconFlowCompletionsConfig();
    }


    @Bean
    @ConfigurationProperties(prefix = "appframe.zhipu.chat")
    @ConditionalOnProperty(prefix = "appframe.zhipu.chat", name = "uri")
    public ZhipuChatConfig zhipuChatConfig() {
        return new ZhipuChatConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.doubao.completions")
    @ConditionalOnProperty(prefix = "appframe.doubao.completions", name = "uri")
    public DoubaoCompletionsConfig doubaoCompletionsConfig() {
        return new DoubaoCompletionsConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.doubao.image")
    @ConditionalOnProperty(prefix = "appframe.doubao.image", name = "uri")
    public DoubaoImageConfig doubaoImageConfig() {
        return new DoubaoImageConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.doubao.video")
    @ConditionalOnProperty(prefix = "appframe.doubao.video", name = "uri")
    public DoubaoVideoConfig doubaoVideoConfig() {
        return new DoubaoVideoConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.embedding")
    @ConditionalOnProperty(prefix = "appframe.yayi.embedding", name = "uri")
    public YayiEmbeddingConfig yayiEmbeddingConfig() {
        return new YayiEmbeddingConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.generate128k")
    @ConditionalOnProperty(prefix = "appframe.yayi.generate128k", name = "uri")
    public Yayi128kConfig yayi128kConfig() {
        return new Yayi128kConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.rearrange")
    @ConditionalOnProperty(prefix = "appframe.yayi.rearrange", name = "uri")
    public RearrangeConfig rearrangeConfig() {
        return new RearrangeConfig();
    }
    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.question")
    @ConditionalOnProperty(prefix = "appframe.yayi.question", name = "uri")
    public QuestionConfig questionConfig() {
        return new QuestionConfig();
    }
    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.urltext")
    @ConditionalOnProperty(prefix = "appframe.yayi.urltext", name = "uri")
    public Url2TextConfig url2TextConfig() {
        return new Url2TextConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.dialogue")
    @ConditionalOnProperty(prefix = "appframe.yayi.dialogue", name = "uri")
    public YayiDialogueFilterConfig dialogueFilterConfig() {
        return new YayiDialogueFilterConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.plugin")
    @ConditionalOnProperty(prefix = "appframe.yayi.plugin", name = "uri")
    public YayiPluginSelectionModelConfig yayiPluginSelectionModelConfig() {
        return new YayiPluginSelectionModelConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "appframe.yayi.index")
    @ConditionalOnProperty(prefix = "appframe.yayi.index", name = "uri")
    public YayiContentIndexConfig yayiContentIndexConfig() {
        return new YayiContentIndexConfig();
    }

    @Bean
    public OkHttpClient wgOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 获取OkHttpClient
     * @param yayiParam
     * @return
     */
    public static OkHttpClient getOkHttpClient(YayiParam yayiParam) {
        if (null != yayiParam && null != yayiParam.getConnectTimeout() && null != yayiParam.getReadTimeout()) {
            return new OkHttpClient.Builder()
                    .connectTimeout(yayiParam.getConnectTimeout(), TimeUnit.SECONDS)
                    .readTimeout(yayiParam.getReadTimeout(), TimeUnit.SECONDS)
                    .build();
        }
        return SpringUtil.getBean(OkHttpClient.class);
    }

    @Bean(StringConstans.YAYI_30B)
    @ConditionalOnBean(name = "yayi30BConfig")
    public Yayi30BStrategy yayi30BStrategy() {
        return new Yayi30BStrategy();
    }

    @Bean(StringConstans.DIALOGUE_FILTER)
    @ConditionalOnBean(name = "dialogueFilterConfig")
    public DialogueFilterStrategy dialogueFilterStrategy() {
        return new DialogueFilterStrategy();
    }
    @Bean(StringConstans.YAYI_EMBEDDING)
    @ConditionalOnBean(name = "yayiEmbeddingConfig")
    public EmbeddingStrategy yayiEmbeddingStrategy() {
        return new EmbeddingStrategy();
    }
    @Bean(StringConstans.URL_TEXT)
    @ConditionalOnBean(name = "url2TextConfig")
    public UrlTextStrategy urlTextStrategy() {
        return new UrlTextStrategy();
    }
    @Bean(StringConstans.YAYI_13B)
    @ConditionalOnBean(name = "yayi13BConfig")
    public Yayi13BStrategy yayi13BStrategy() {
        return new Yayi13BStrategy();
    }
    @Bean(StringConstans.YAYI_WEB_PROMPT)
    @ConditionalOnBean(name = "webPromptConfig")
    public WebPromptStrategy yayiPromptStrategy() {
        return new WebPromptStrategy();
    }

    @Bean(StringConstans.YAYI_128K)
    @ConditionalOnBean(name = "yayi128kConfig")
    public Yayi128KStrategy yayi128KStrategy() {
        return new Yayi128KStrategy();
    }

    @Bean(StringConstans.PLUGIN_SELECTION_MODEL)
    @ConditionalOnBean(name = "yayiPluginSelectionModelConfig")
    public YayiPluginSelectionModelStrategy yayiPluginSelectionModelStrategy() {
        return new YayiPluginSelectionModelStrategy();
    }

    @Bean(StringConstans.CONTENT_INDEX)
    @ConditionalOnBean(name = "yayiContentIndexConfig")
    public YayiContentIndexStrategy yayiContentIndexStrategy() {
        return new YayiContentIndexStrategy();
    }

    @Bean(StringConstans.KIMI_COMPLETION)
    @ConditionalOnBean(name = "kimiCompletionsConfig")
    public KimiCompletionStrategy kimiCompletionStrategy() {
        return new KimiCompletionStrategy();
    }

    @Bean(StringConstans.DEEPSEEK_COMPLETION)
    @ConditionalOnBean(name = "deepseekCompletionsConfig")
    public DeepseekCompletionStrategy deepseekCompletionStrategy() {
        return new DeepseekCompletionStrategy();
    }
    @Bean(StringConstans.SILICON_FLOW_COMPLETION)
    @ConditionalOnBean(name = "siliconflowConfig")
    public SiliconFlowCompletionStrategy siliconFlowCompletionStrategy() {
        return new SiliconFlowCompletionStrategy();
    }

    @Bean(StringConstans.DOUBAO_COMPLETION_STRATEGY)
    @ConditionalOnBean(name = "doubaoCompletionsConfig")
    public DoubaoStrategy doubaoStrategy() {
        return new DoubaoStrategy();
    }

    @Bean(StringConstans.MINMAX_STRATEGY)
    @ConditionalOnBean(name = "minmaxCompletionsConfig")
    public MinmaxStrategy minmaxStrategy() {
        return new MinmaxStrategy();
    }

    @Bean(StringConstans.DOUBAO_IMAGE_STRATEGY)
    @ConditionalOnBean(name = "doubaoImageConfig")
    public DoubaoImageStrategy doubaoImageStrategy() {
        return new DoubaoImageStrategy();
    }

    @Bean(StringConstans.MINMAX_IMAGE)
    @ConditionalOnBean(name = "minmaxImageConfig")
    public MinmaxImageStrategy minmaxImageStrategy() {
        return new MinmaxImageStrategy();
    }

    @Bean(StringConstans.MINMAX_MUSIC)
    @ConditionalOnBean(name = "minmaxMusicConfig")
    public MinmaxMusicStrategy minmaxMusicStrategy() {
        return new MinmaxMusicStrategy();
    }

    @Bean(StringConstans.MINMAX_VIDEO)
    @ConditionalOnBean(name = "minmaxVideoConfig")
    public MinmaxVideoStrategy minmaxVideoStrategy() {
        return new MinmaxVideoStrategy();
    }

    @Bean(StringConstans.DOUBAO_VIDEO_STRATEGY)
    @ConditionalOnBean(name = "doubaoVideoConfig")
    public DoubaoVideoStrategy doubaoVideoStrategy() {
        return new DoubaoVideoStrategy();
    }

    @Bean(StringConstans.GENERAL_COMPLETION_STRATEGY)
    public GeneralStrategy generalStrategy() {
        return new GeneralStrategy();
    }

    @Bean(StringConstans.WENXIN)
    @ConditionalOnBean(name = "wenxinChatConfig")
    public WenxinChatStrategy wenxinChatStrategy() {
        return new WenxinChatStrategy();
    }


    @Bean(StringConstans.ZHIPU)
    @ConditionalOnBean(name = "zhipuChatConfig")
    public ZhipuStrategy zhipuStrategy() {
        return new ZhipuStrategy();
    }

    @Bean(StringConstans.REARRANGE)
    @ConditionalOnBean(name = "rearrangeConfig")
    public RearrangeStrategy rearrangeStrategy() {
        return new RearrangeStrategy();
    }

    @Bean(StringConstans.VOLCENGINE_REARRANGE)
    @ConditionalOnBean(name = "volcengineRearrangeConfig")
    public VolcengineRearrangeStrategy volcengineRearrangeStrategy() {
        return new VolcengineRearrangeStrategy();
    }
    @Bean(StringConstans.VISUAL_STRATEGY)
    @ConditionalOnBean(name = "visualConfig")
    public VisualStrategy visualStrategy() {
        return new VisualStrategy();
    }

    @Bean(StringConstans.CHART_STRATEGY)
    @ConditionalOnBean(name = "chartConfig")
    public ChartStrategy chartStrategy() {
        return new ChartStrategy();
    }

    @Bean(StringConstans.MATH_MODEL_STRATEGY)
    @ConditionalOnBean(name = "mathModelConfig")
    public MathModelStrategy mathModelStrategy() {
        return new MathModelStrategy();
    }
    @Bean(StringConstans.QUESTION)
    @ConditionalOnBean(name = "questionConfig")
    public QuestionStrategy questionStrategy() {
        return new QuestionStrategy();
    }

    @Bean(StringConstans.DOCUMENT)
    @ConditionalOnBean(name = "documentConfig")
    public DocumentStrategy documentStrategy() {
        return new DocumentStrategy();
    }
    @Bean(StringConstans.AI_IMAGE)
    @ConditionalOnBean(name = "aiImageConfig")
    public AiImageStrategy aiImageStrategy() {
        return new AiImageStrategy();
    }
    @Bean(StringConstans.CONTENT_PARSING)
    @ConditionalOnBean(name = "contentParsingConfig")
    public ContentParsingStrategy contentParsingStrategy() {
        return new ContentParsingStrategy();
    }
    @Bean(StringConstans.YAYI_TRANSLATION)
    @ConditionalOnBean(name = "yayiTranslationConfig")
    public YayiTranslationStrategy yayiTranslationStrategy() {
        return new YayiTranslationStrategy();
    }
    @Bean(StringConstans.KNOWLEDGE)
    @ConditionalOnBean(name = "knowledgeConfig")
    public KnowledgeStrategy knowledgeStrategy() {
        return new KnowledgeStrategy();
    }

    @Bean(StringConstans.KNOWLEDGE_SPLIT)
    @ConditionalOnBean(name = "knowledgeSplitConfig")
    public KnowledgeStrategy knowledgeSplitStrategy() {
        return new KnowledgeStrategy();
    }
    @Bean(StringConstans.SECURITY)
    @ConditionalOnBean(name = "securityConfig")
    public SecurityStrategy securityStrategy() {
        return new SecurityStrategy();
    }
    @Bean(StringConstans.CONTENT_PARSING_NEW_VERSION)
    @ConditionalOnBean(name = "contentParsingNewVersionConfig")
    public ContentParsingNewVersionStrategy contentParsingNewVersionStrategy() {
        return new ContentParsingNewVersionStrategy();
    }

    @Bean(StringConstans.IMAGE_IDENTIFY)
    @ConditionalOnBean(name = "imageIdentifyConfig")
    public ImageIdentifyStrategy imageIdentifyStrategy() {
        return new ImageIdentifyStrategy();
    }

    @Override
    public void initialize(GenericApplicationContext applicationContext) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:META-INF/maven/com.wenge/appframe-yayi-sersver/pom.xml");
        try (InputStream inputStream = resource.getInputStream()) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model read = reader.read(inputStream);
            String version = read.getVersion();
            log.info("〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓 appframe-yayi-sersver (" + version + ")  〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓");
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
        }
    }
}
