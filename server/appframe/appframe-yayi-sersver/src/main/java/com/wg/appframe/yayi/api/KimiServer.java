package com.wg.appframe.yayi.api;

import com.wg.appframe.yayi.constants.StringConstans;
import com.wg.appframe.yayi.param.KimiCompletionParam;
import com.wg.appframe.yayi.result.KimiCompletionResult;
import com.wg.appframe.yayi.strategy.KimiCompletionStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;

@Slf4j
public class KimiServer {
    
    @Autowired(required = false)
    private KimiCompletionStrategy kimiCompletionStrategy;

    /**
     * 调用completions接口
     *
     * @param content
     * @return
     */
    public KimiCompletionResult generate(String content) {
        if (null == kimiCompletionStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.kimi.completions >>>>>>>>>>>>");
            return new KimiCompletionResult();
        }

        KimiCompletionParam param = new KimiCompletionParam();
        param.setStream(false);
        return kimiCompletionStrategy.run(content, param, null);
    }

    /**
     * 调用completions接口，返回文本回答
     *
     * @param content
     * @return
     */
    public String generateStr(String content) {
        KimiCompletionResult result = generate(content);
        try {
            return result.getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            log.info("===> generateStr.error:{}", e.getMessage());
        }
        return StringConstans.BLANK;
    }

    /**
     * 调用completions接口
     *
     * @param content
     * @return
     */
    public KimiCompletionResult generate(String content, KimiCompletionParam param) {
        if (null == kimiCompletionStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.kimi.completions >>>>>>>>>>>>");
            return new KimiCompletionResult();
        }
        if (null == param) {
            param = new KimiCompletionParam();
        }
        param.setStream(false);
        return kimiCompletionStrategy.run(content, param, null);
        
    }

    /**
     * 调用completions接口，返回文本回答
     *
     * @param content
     * @return
     */
    public String generateString(String content, KimiCompletionParam param) {
        KimiCompletionResult result = generate(content, param);
        try {
            return result.getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            log.info("===> generateString.error:{}", e.getMessage());
        }
        return StringConstans.BLANK;
    }

    /**
     * 调用completions接口
     *
     * @param content
     * @return
     */
    public void generateStream(String content, Consumer<KimiCompletionResult> consumer) {
        if (null == kimiCompletionStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.kimi.completions >>>>>>>>>>>>");
            return;
        }
        KimiCompletionParam param = new KimiCompletionParam();
        param.setStream(true);
         kimiCompletionStrategy.run(content, param, consumer);
    }

    /**
     * 调用completions接口，返回文本
     *
     * @param content
     * @return
     */
    public void generateStreamStr(String content, Consumer<String> consumer) {
        generateStream(content, p -> {
            try {
                String result = p.getChoices().get(0).getDelta().getContent();
                consumer.accept(result);
            } catch (Exception e) {
                log.error("===> generateStreamStr.error:{}", e.getMessage());
                consumer.accept(StringConstans.BLANK);
            }
        });
    }

    /**
     * 调用completions接口
     *
     * @param content
     * @return
     */
    public void generateStream(String content, KimiCompletionParam param, Consumer<KimiCompletionResult> consumer) {
        if (null == kimiCompletionStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.kimi.completions >>>>>>>>>>>>");
            return;
        }
        if (null == param) {
            param = new KimiCompletionParam();
        }
        param.setStream(true);
        kimiCompletionStrategy.run(content, param, consumer);
    }

    /**
     * 调用completions接口，返回文本
     *
     * @param content
     * @return
     */
    public void generateStreamStr(String content, KimiCompletionParam param, Consumer<String> consumer) {
        generateStream(content, param, p -> {
            try {
                String result = p.getChoices().get(0).getDelta().getContent();
                consumer.accept(result);
            } catch (Exception e) {
                log.error("===> generateStreamStr.error:{}", e.getMessage());
                consumer.accept(StringConstans.BLANK);
            }
        });

    }
}
