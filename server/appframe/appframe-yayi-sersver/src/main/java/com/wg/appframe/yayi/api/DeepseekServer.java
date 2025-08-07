package com.wg.appframe.yayi.api;

import com.wg.appframe.yayi.constants.StringConstans;
import com.wg.appframe.yayi.param.DeepseekCompletionParam;
import com.wg.appframe.yayi.result.DeepseekCompletionResult;
import com.wg.appframe.yayi.strategy.DeepseekCompletionStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;

@Slf4j
public class DeepseekServer {
    
    @Autowired(required = false)
    private DeepseekCompletionStrategy deepseekCompletionStrategy;

    /**
     * 调用completions接口
     *
     * @param content
     * @return
     */
    public DeepseekCompletionResult generate(String content) {
        if (null == deepseekCompletionStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.deepseek.completions >>>>>>>>>>>>");
            return new DeepseekCompletionResult();
        }

        DeepseekCompletionParam param = new DeepseekCompletionParam();
        param.setStream(false);
        return deepseekCompletionStrategy.run(content, param, null);
    }

    /**
     * 调用completions接口，返回文本回答
     *
     * @param content
     * @return
     */
    public String generateStr(String content) {
        DeepseekCompletionResult result = generate(content);
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
    public DeepseekCompletionResult generate(String content, DeepseekCompletionParam param) {
        if (null == deepseekCompletionStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.deepseek.completions >>>>>>>>>>>>");
            return new DeepseekCompletionResult();
        }
        if (null == param) {
            param = new DeepseekCompletionParam();
        }
        param.setStream(false);
        return deepseekCompletionStrategy.run(content, param, null);
        
    }

    /**
     * 调用completions接口，返回文本回答
     *
     * @param content
     * @return
     */
    public String generateString(String content, DeepseekCompletionParam param) {
        DeepseekCompletionResult result = generate(content, param);
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
    public void generateStream(String content, Consumer<DeepseekCompletionResult> consumer) {
        if (null == deepseekCompletionStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.deepseek.completions >>>>>>>>>>>>");
            return;
        }
        DeepseekCompletionParam param = new DeepseekCompletionParam();
        param.setStream(true);
         deepseekCompletionStrategy.run(content, param, consumer);
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
    public void generateStream(String content, DeepseekCompletionParam param, Consumer<DeepseekCompletionResult> consumer) {
        if (null == deepseekCompletionStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.deepseek.completions >>>>>>>>>>>>");
            return;
        }
        if (null == param) {
            param = new DeepseekCompletionParam();
        }
        param.setStream(true);
        deepseekCompletionStrategy.run(content, param, consumer);
    }

    /**
     * 调用completions接口，返回文本
     *
     * @param content
     * @return
     */
    public void generateStreamStr(String content, DeepseekCompletionParam param, Consumer<String> consumer) {
        generateStream(content, param,p -> {
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
